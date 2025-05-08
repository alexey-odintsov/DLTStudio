package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntry
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineStateEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.StateEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class StateEntriesExtractorTest {

    private val extractor = StateEntriesExtractor()

    @Test
    fun `Test StateEntriesExtractor using named groups`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "User 10 state changed from LOADING to RUNNING"
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
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "User 10 state changed from LOADING to RUNNING"
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