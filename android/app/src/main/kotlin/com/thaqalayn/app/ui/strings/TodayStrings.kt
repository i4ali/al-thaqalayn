package com.thaqalayn.app.ui.strings

import com.thaqalayn.app.model.DailyChallengeFormat

/** Chrome copy for the Today tab (iOS TodayStrings). English only, plain spelling, no em dash. */
object TodayStrings {
    val greeting = "Assalamu alaykum"

    fun greeting(name: String): String = if (name.isEmpty()) greeting else "$greeting, $name"

    val today = "Today"

    val reminderEyebrow = "A reminder for today"

    val continueReading = "Continue reading"

    val duaOfTheDay = "Du'a of the day"

    val startJourney = "Start your journey"

    val openFatiha = "Open Surah Al-Fatiha"

    val begin = "Begin"

    val resume = "Resume"

    fun verseOf(n: Int, total: Int) = "Verse $n of $total"

    fun percentComplete(p: Int) = "$p% complete"
}

/** Bookmark spotlight card copy (iOS BookmarkSpotlightStrings). */
object BookmarkSpotlightStrings {
    val eyebrow = "Your bookmarks"

    fun allBookmarks(n: Int) = "All bookmarks ($n)"
}

/** Daily challenge copy (iOS DailyChallengeStrings). */
object DailyChallengeStrings {
    val dailyChallenge = "Daily Challenge"

    val doneForToday = "Done for today"

    val correct = "Correct"

    val notQuite = "Not quite"

    val flipCard = "Tap to flip"

    val gotIt = "Got it"

    val reviewAgain = "Review again"

    val trueLabel = "True"

    val falseLabel = "False"

    fun dayUnit(count: Int): String = if (count == 1) "1 day" else "$count days"

    fun streakLabel(count: Int) = "${dayUnit(count)} streak"

    fun teaser(format: DailyChallengeFormat): String = when (format) {
        DailyChallengeFormat.multipleChoice -> "Pick the right answer"
        DailyChallengeFormat.trueFalse -> "True or false?"
        DailyChallengeFormat.flashcard -> "Flashcard - flip to test yourself"
        DailyChallengeFormat.fillInBlank -> "Fill in the blank"
    }

    val completionTitle = "Well done"

    val doneButton = "Done"
}

/** Daily crossword copy (iOS DailyCrosswordStrings). */
object DailyCrosswordStrings {
    val dailyCrossword = "Daily Crossword"

    val teaser = "6 words to solve"

    val doneForToday = "Done for today"

    val solved = "Solved!"

    val comeBackTomorrow = "Come back tomorrow for a new puzzle."

    val across = "Across"

    val down = "Down"

    val hint = "Hint"

    val done = "Done"

    val words = "words"

    fun streakLabel(n: Int) = "$n-day streak"
}
