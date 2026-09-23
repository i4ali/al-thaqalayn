package com.thaqalayn.app.data

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Date

// Port of iOS Shared/DailyVerseCore.swift: the pure date -> verse selection behind
// the Today reminder hero and the daily-verse notification. The algorithm (seed,
// epoch, day count, theme-spaced order, sacred-day override) matches iOS exactly,
// so the same date picks the same verse on both platforms.

/** The whole pool, decoded from daily_verses.json. */
@Serializable
data class DailyVersePool(
    val version: Int,
    val themes: List<String>,
    val verses: List<DailyVerseEntry>,
    val sacredDays: List<SacredDay>
)

/**
 * One curated reference. Carries no verse text - Arabic and translation hydrate
 * from quran_data.json at read time.
 */
@Serializable
data class DailyVerseEntry(
    val id: Int,
    val surah: Int,
    val verse: Int,
    /** Vocabulary key. Drives the no-two-days-running spacing rule. */
    val themeKey: String,
    val themeEn: String
)

/** A Hijri date that overrides the pool. */
@Serializable
data class SacredDay(
    val month: Int,
    val day: Int,
    val surah: Int,
    val verse: Int,
    val occasionEn: String,
    val themeEn: String
)

/** What a surface renders. [occasionEn] is non-null only on a sacred day. */
data class DailyVerseSelection(
    val surah: Int,
    val verse: Int,
    /** The vocabulary key, or null on a sacred day (not drawn from the pool). */
    val themeKey: String?,
    val themeEn: String,
    val occasionEn: String?
) {
    val id: String get() = "$surah:$verse"

    companion object {
        fun of(entry: DailyVerseEntry) = DailyVerseSelection(
            surah = entry.surah, verse = entry.verse,
            themeKey = entry.themeKey, themeEn = entry.themeEn, occasionEn = null
        )

        fun of(sacred: SacredDay) = DailyVerseSelection(
            surah = sacred.surah, verse = sacred.verse,
            themeKey = null, themeEn = sacred.themeEn, occasionEn = sacred.occasionEn
        )
    }
}

/**
 * Pure date -> verse selection. Holds only the permutation cache; not
 * thread-safe, so each consumer owns its instance (the provider guards it).
 */
class DailyVerseSelector(pool: DailyVersePool) {
    val pool: List<DailyVerseEntry> = pool.verses
    val sacredDays: List<SacredDay> = pool.sacredDays

    /** Keyed by cycle: a cycle's opening theme depends on the previous cycle's closing theme. */
    private val permutationCache = mutableMapOf<Int, List<Int>>()

    init {
        require(this.pool.isNotEmpty()) { "daily_verses.json must contain at least one verse" }
    }

    /** The verse for any date. Pure - writes no state beyond the cache. */
    fun verseFor(date: Date): DailyVerseSelection {
        sacredDayFor(date)?.let { return DailyVerseSelection.of(it) }
        return DailyVerseSelection.of(pooledEntry(date))
    }

    private fun sacredDayFor(date: Date): SacredDay? {
        val (month, day) = IslamicCalendarManager.hijriMonthDay(date)
        return sacredDays.firstOrNull { it.month == month && it.day == day }
    }

    private fun pooledEntry(date: Date): DailyVerseEntry {
        val n = pool.size
        val index = dayIndex(date)
        val order = permutation(index / n)
        return pool[order[index % n]]
    }

    /**
     * Whole days from the epoch, in the user's own timezone - the same count
     * Foundation's `dateComponents([.day], from: epoch, to: startOfDay)` gives.
     */
    private fun dayIndex(date: Date): Int {
        val zone = ZoneId.systemDefault()
        val start = ZonedDateTime.ofInstant(date.toInstant(), zone).truncatedTo(ChronoUnit.DAYS)
        val epoch = ZonedDateTime.ofInstant(EPOCH, zone)
        return maxOf(0, ChronoUnit.DAYS.between(epoch, start).toInt())
    }

    /** The pool order for a cycle, built forward so the year seam keeps the spacing rule. */
    private fun permutation(cycle: Int): List<Int> {
        permutationCache[cycle]?.let { return it }

        var start = cycle
        while (start > 0 && permutationCache[start - 1] == null) start -= 1

        for (current in start..cycle) {
            val previousTheme = if (current > 0) {
                permutationCache[current - 1]?.lastOrNull()?.let { pool[it].themeKey }
            } else {
                null
            }
            permutationCache[current] = spacedOrder(current, previousTheme)
        }
        return permutationCache[cycle] ?: pool.indices.toList()
    }

    /**
     * Theme-spaced, verse-randomised order: greedy by largest remaining theme
     * bucket, ties broken with the seeded RNG, each bucket pre-shuffled.
     */
    private fun spacedOrder(cycle: Int, previousTheme: String?): List<Int> {
        val rng = SplitMix64(cycle.toLong().toULong() + 0x9E3779B97F4A7C15uL)

        val buckets = mutableMapOf<String, MutableList<Int>>()
        for (index in pool.indices) {
            buckets.getOrPut(pool[index].themeKey) { mutableListOf() }.add(index)
        }
        // Sorted keys keep the shuffle order identical on every device.
        for (key in buckets.keys.sorted()) {
            val members = buckets.getValue(key)
            var i = members.size - 1
            while (i > 0) {
                val j = (rng.next() % (i + 1).toULong()).toInt()
                val tmp = members[i]
                members[i] = members[j]
                members[j] = tmp
                i -= 1
            }
        }

        val result = ArrayList<Int>(pool.size)
        var last = previousTheme
        while (result.size < pool.size) {
            val available = buckets.filter { it.value.isNotEmpty() && it.key != last }
            val maxCount = available.values.maxOfOrNull { it.size }
            if (maxCount == null) {
                // Only `last` still has entries; unreachable while the validator's theme cap holds.
                for (key in buckets.keys.sorted()) result.addAll(buckets.getValue(key))
                break
            }
            val tied = available.filter { it.value.size == maxCount }.keys.sorted()
            val chosen = tied[(rng.next() % tied.size.toULong()).toInt()]
            result.add(buckets.getValue(chosen).removeAt(0))
            last = chosen
        }
        return result
    }

    companion object {
        /** Fixed forever. Moving it reshuffles every user's year. */
        private val EPOCH: Instant = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant()
    }
}

/** Deterministic, portable PRNG (same constants as the iOS SplitMix64). */
private class SplitMix64(seed: ULong) {
    private var state = seed

    fun next(): ULong {
        state += 0x9E3779B97F4A7C15uL
        var z = state
        z = (z xor (z shr 30)) * 0xBF58476D1CE4E5B9uL
        z = (z xor (z shr 27)) * 0x94D049BB133111EBuL
        return z xor (z shr 31)
    }
}
