package com.alekso.dltstudio.tests.timeline.extractors

import com.alekso.dltstudio.tests.Utils
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.StateEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class StateEntriesExtractorTest {

    private val extractor = StateEntriesExtractor()

    @Test
    fun `Test StateEntriesExtractor using named groups`() {
        val dltMessage = Utils.dltMessage(
            timeStampUs = 1234567890L, payload = "User 10 state changed from LOADING to RUNNING"
        )
        val pattern = """User\s(?<key>\d+)\sstate changed from (?<value>.*) to (?<oldvalue>.*)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineStateEntry(1234567890L, "10", Pair("LOADING", "RUNNING")),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry
        ).toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test StateEntriesExtractor with dynamic group names`() {
        val dltMessage = Utils.dltMessage(
            timeStampUs = 1234567890L, payload = "User 10 state changed from LOADING to RUNNING"
        )
        val pattern = """User\s(\d+)\sstate changed from (.*) to (.*)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineStateEntry(1234567890L, "10", Pair("LOADING", "RUNNING")),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries
        ).toSet()
        assertEquals(expected, actual)
    }

}