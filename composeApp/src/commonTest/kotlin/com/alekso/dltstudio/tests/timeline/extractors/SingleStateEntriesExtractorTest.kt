package com.alekso.dltstudio.tests.timeline.extractors

import com.alekso.dltstudio.tests.Utils
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.SingleStateEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class SingleStateEntriesExtractorTest {

    private val extractor = SingleStateEntriesExtractor()

    @Test
    fun `Test SingleStateEntriesExtractor using named groups`() {
        val dltMessage = Utils.dltMessage(
            timeStampNano = 1234567890L, payload = "User 10 state changed to RUNNING"
        )
        val pattern = """User\s(?<key>\d+)\sstate changed to (?<value>.*)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineSingleStateEntry(1234567890L, "10", "RUNNING"),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry
        ).toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test SingleStateEntriesExtractor with dynamic group names`() {
        val dltMessage = Utils.dltMessage(
            timeStampNano = 1234567890L, payload = "User 10 state changed to RUNNING"
        )
        val pattern = """User\s(\d+)\sstate changed to (.*)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineSingleStateEntry(1234567890L, "10", "RUNNING"),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries
        ).toSet()
        assertEquals(expected, actual)
    }

}