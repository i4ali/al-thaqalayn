#!/usr/bin/env python3
"""
Publish an Android App Bundle to Google Play via the Play Developer Publishing API
(androidpublisher v3) - the Android counterpart to the App Store Connect upload flow.

Auth is a Google Cloud service-account JSON key whose email has been granted release
permissions in Play Console (Users and permissions). The key is NEVER committed; it
lives outside the repo (default: ~/Documents/googleplay_publisher.json), like the
App Store Connect .p8.

The API is transactional ("edits"): insert an edit -> upload the AAB -> assign its
versionCode to a track with a release -> commit. Committing is the submission;
Google runs review automatically. Nothing is published until --commit is passed;
without it the script uploads, validates, and abandons the edit (a real dry run of
the whole pipeline that publishes nothing).

Setup that backs this script (already done once):
  - Cloud project 'althaqalayn-play', androidpublisher API enabled.
  - Service account play-publisher@althaqalayn-play.iam.gserviceaccount.com.
  - That account invited in Play Console with app-scoped release permissions.

Usage:
  # Dry run (default): build the pipeline, publish nothing
  python3 android/scripts/play_publish.py --track internal

  # Real release to internal testers
  python3 android/scripts/play_publish.py --track internal --commit

  # Closed (alpha) with release notes, real
  python3 android/scripts/play_publish.py --track alpha --notes-file notes.json --commit

  # Production staged rollout at 20%
  python3 android/scripts/play_publish.py --track production --status inProgress \
      --rollout 0.2 --notes-file notes.json --commit

notes.json is {"en-US": "...", "ur": "...", ...}; --notes "text" is shorthand for en-US.
"""

import argparse
import json
import os
import socket
import ssl
import sys
import time
from pathlib import Path

from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload

# Uploads over this slow/flaky uplink stall mid-chunk. Keep the per-socket timeout
# short so a stall surfaces in ~2 min (not 10), then the resume loop below re-issues
# next_chunk(), which the resumable protocol continues from the server's last byte.
socket.setdefaulttimeout(120)

SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]
DEFAULT_PACKAGE = "com.althaqalayn.app"
DEFAULT_KEY = os.environ.get("GOOGLE_PLAY_KEY") or os.path.expanduser(
    "~/Documents/googleplay_publisher.json"
)
# android/ is the parent of android/scripts/
ANDROID_DIR = Path(__file__).resolve().parent.parent
DEFAULT_AAB = ANDROID_DIR / "app/build/outputs/bundle/release/app-release.aab"

VALID_TRACKS = {"internal", "alpha", "beta", "production"}


def fail(msg: str, code: int = 1):
    print(f"ERROR: {msg}", file=sys.stderr)
    sys.exit(code)


def build_release_notes(args) -> list:
    if args.notes_file:
        data = json.loads(Path(args.notes_file).read_text(encoding="utf-8"))
        return [{"language": lang, "text": text} for lang, text in data.items()]
    if args.notes:
        return [{"language": args.notes_lang, "text": args.notes}]
    return []


def main():
    p = argparse.ArgumentParser(description="Publish an AAB to Google Play")
    p.add_argument("--track", default="internal", choices=sorted(VALID_TRACKS))
    p.add_argument("--aab", default=str(DEFAULT_AAB), help="Path to the signed .aab")
    p.add_argument("--package", default=DEFAULT_PACKAGE)
    p.add_argument("--key", default=DEFAULT_KEY, help="Service-account JSON key path")
    p.add_argument("--status", default="completed",
                   choices=["completed", "inProgress", "draft", "halted"],
                   help="Release status on the track")
    p.add_argument("--rollout", type=float, default=None,
                   help="userFraction for a staged (inProgress) rollout, 0..1")
    p.add_argument("--release-name", default=None,
                   help="Release name (default: the bundle's versionCode)")
    p.add_argument("--notes", default=None, help="Release notes text (single language)")
    p.add_argument("--notes-lang", default="en-US", help="Language for --notes")
    p.add_argument("--notes-file", default=None, help="JSON {lang: text} of release notes")
    p.add_argument("--commit", action="store_true",
                   help="Actually publish. Without it, uploads + validates + abandons.")
    p.add_argument("--no-review", action="store_true",
                   help="Commit with changesNotSentForReview=true (advanced)")
    args = p.parse_args()

    if args.status == "inProgress" and args.rollout is None:
        fail("--status inProgress needs --rollout (e.g. --rollout 0.1)")
    aab = Path(args.aab)
    if not aab.exists():
        fail(f"AAB not found: {aab}\nBuild it first: (cd android && ./gradlew bundleRelease)")
    if not Path(args.key).exists():
        fail(f"Service-account key not found: {args.key}")

    creds = service_account.Credentials.from_service_account_file(args.key, scopes=SCOPES)
    service = build("androidpublisher", "v3", credentials=creds, cache_discovery=False)
    edits = service.edits()

    mode = "PUBLISH" if args.commit else "DRY RUN (nothing will be published)"
    print(f"== Play publish: {args.package} -> track '{args.track}' [{mode}] ==")
    print(f"   AAB: {aab} ({aab.stat().st_size/1_000_000:.1f} MB)")

    edit_id = edits.insert(packageName=args.package, body={}).execute()["id"]
    committed = False
    try:
        print("   uploading bundle...")
        media = MediaFileUpload(str(aab), mimetype="application/octet-stream",
                                resumable=True, chunksize=2 * 1024 * 1024)
        request = edits.bundles().upload(
            packageName=args.package, editId=edit_id, media_body=media
        )
        bundle = None
        stalls = 0
        # Resilient resumable upload: on a transient network stall/error, wait briefly
        # and re-issue next_chunk() - the resumable session continues from the last
        # byte the server acknowledged, so progress is never lost to a restart.
        while bundle is None:
            try:
                status, bundle = request.next_chunk(num_retries=3)
                if status:
                    print(f"   upload {int(status.progress() * 100)}%")
                stalls = 0
            except (HttpError, ssl.SSLError, socket.timeout, TimeoutError, OSError) as e:
                stalls += 1
                if stalls > 30:
                    raise
                print(f"   upload stalled ({type(e).__name__}); resuming (retry {stalls})...")
                time.sleep(3)
        version_code = bundle["versionCode"]
        print(f"   uploaded versionCode {version_code}")

        release = {
            "status": args.status,
            "versionCodes": [str(version_code)],
        }
        release["name"] = args.release_name or str(version_code)
        if args.rollout is not None:
            release["userFraction"] = args.rollout
        notes = build_release_notes(args)
        if notes:
            release["releaseNotes"] = notes

        edits.tracks().update(
            packageName=args.package, editId=edit_id, track=args.track,
            body={"track": args.track, "releases": [release]},
        ).execute()
        print(f"   assigned to '{args.track}': status={args.status}"
              + (f", rollout={args.rollout}" if args.rollout is not None else ""))

        # Validate the edit either way; it catches most problems before commit.
        edits.validate(packageName=args.package, editId=edit_id).execute()
        print("   edit validated OK")

        if args.commit:
            edits.commit(
                packageName=args.package, editId=edit_id,
                changesNotSentForReview=args.no_review,
            ).execute()
            committed = True
            print(f"COMMITTED - versionCode {version_code} released to '{args.track}'."
                  " Google review runs automatically.")
        else:
            print("DRY RUN complete - upload + track assignment + validation all succeeded."
                  " Re-run with --commit to publish for real.")
    finally:
        if not committed:
            try:
                edits.delete(packageName=args.package, editId=edit_id).execute()
                print("   edit abandoned (no changes published).")
            except Exception:
                # An uncommitted edit expires on its own; never mask the real error.
                pass


if __name__ == "__main__":
    try:
        main()
    except HttpError as e:
        fail(f"Play API HTTP {e.resp.status}: {e}")
