#!/usr/bin/env python3
"""Build themes_index.json for Quran search from the shared iOS tafsir data.

The iOS app builds theme search entries at runtime from every tafsir_N.json
quickOverview block (all 114 files preloaded in memory). Android keeps tafsir
lazy-loaded per surah, so this script precomputes the tiny theme index at build
time instead. Re-run whenever quickOverview data changes:

    python3 android/scripts/generate_theme_index.py
"""
import json
import os

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
DATA = os.path.join(ROOT, "Thaqalayn", "Thaqalayn", "Data")
OUT = os.path.join(ROOT, "android", "app", "src", "main", "assets", "themes_index.json")

quran = json.load(open(os.path.join(DATA, "quran_data.json")))
surah_names = {s["number"]: s["englishName"] for s in quran["surahs"]}

entries = []
for n in range(1, 115):
    path = os.path.join(DATA, f"tafsir_{n}.json")
    if not os.path.exists(path):
        continue
    tafsir = json.load(open(path))
    for verse_key, verse in tafsir.items():
        overview = verse.get("quickOverview")
        if not overview:
            continue
        for c in overview.get("concepts", []):
            entries.append({
                "conceptId": c["id"],
                "title": c["title"],
                "colorHex": c["colorHex"],
                "surahNumber": n,
                "verseNumber": int(verse_key),
                "surahEnglishName": surah_names.get(n, ""),
            })

entries.sort(key=lambda e: (e["surahNumber"], e["verseNumber"]))
json.dump({"themes": entries}, open(OUT, "w"), ensure_ascii=False)
print(f"Wrote {len(entries)} theme entries -> {OUT}")
