package com.thaqalayn.app.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.thaqalayn.app.data.DuaTimings
import com.thaqalayn.app.data.DuaWordPosition
import com.thaqalayn.app.data.SpecialDuaTimingsStore
import kotlin.math.abs

/**
 * Drives the golden word highlight in SpecialDuaDetailScreen: follows DuaStreamPlayer's
 * playback clock through a dua's aligned word timeline (SpecialDuaTimings) and exposes
 * the word/segment being recited. Also the gate for tap-to-seek. Goes inert when
 * another dua (or nothing) is playing, and when the streamed file's duration no longer
 * matches the recording the alignment was made against (stale timings after a duas.org
 * file swap). Mirrors the iOS DuaKaraokeEngine; the detail screen feeds it the stream
 * clock via snapshotFlow. One instance per detail screen.
 */
class DuaKaraokeEngine {
    var currentWord by mutableStateOf<DuaWordPosition?>(null)
        private set
    var currentSegment by mutableStateOf<Int?>(null)
        private set

    var timings: DuaTimings? = null
        private set

    private var duaID: String? = null
    private var durationMismatch = false
    private var lastStreamId: String? = null

    /** Streamed duration may differ slightly from the aligned file's (VBR estimation);
     *  beyond this the recording itself must have changed. */
    private val durationTolerance = 5.0

    /** Bind the engine to one dua for the lifetime of its detail screen. */
    fun configure(duaID: String) {
        if (this.duaID == duaID) return
        this.duaID = duaID
        this.timings = SpecialDuaTimingsStore.timings(duaID)
        durationMismatch = false
        clear()
    }

    /** Whether tapping a segment may seek the stream there: this dua is the one loaded,
     *  its timings exist and match the streamed recording. */
    fun canSeek(streamId: String?): Boolean =
        timings != null && streamId == duaID && !durationMismatch

    /** Feed the stream clock. Called on every stream tick from the detail screen. */
    fun update(time: Double, streamId: String?, streamDuration: Double) {
        if (streamId != lastStreamId) {
            durationMismatch = false          // fresh stream: re-evaluate alignment
            lastStreamId = streamId
        }
        val t = timings
        if (t == null || streamId != duaID) {
            clear()
            return
        }
        if (streamDuration > 0 && abs(streamDuration - t.audioDuration) > durationTolerance) {
            durationMismatch = true
        }
        if (durationMismatch) {
            clear()
            return
        }
        val pos = t.position(time)
        if (pos != currentWord) {
            currentWord = pos
            if (pos?.segment != currentSegment) currentSegment = pos?.segment
        }
    }

    private fun clear() {
        if (currentWord != null) currentWord = null
        if (currentSegment != null) currentSegment = null
    }
}
