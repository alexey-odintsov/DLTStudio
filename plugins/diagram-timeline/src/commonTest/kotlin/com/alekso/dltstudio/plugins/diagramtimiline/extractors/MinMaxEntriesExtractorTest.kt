package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntry
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineFloatEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.MinMaxEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class MinMaxEntriesExtractorTest {

    private val extractor = MinMaxEntriesExtractor()

    @Test
    fun `Test MinMaxEntriesExtractor using named groups`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "12345 67 890"
        )
        val pattern = """(?<g1>\d+)\s(?<g2>\d+)\s(?<g3>\d+)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "g1", 12345f),
            TimeLineFloatEntry(1234567890L, "g2", 67f),
            TimeLineFloatEntry(1234567890L, "g3", 890f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage, pattern.toRegex(), EntriesExtractor.ExtractionType.NamedGroupsManyEntries
        ).toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test MinMaxEntriesExtractor using named groups one entry`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "MaxRSS: 345"
        )
        val pattern = """(?<key>MaxRSS):\s(?<value>\d+)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "MaxRSS", 345f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage, pattern.toRegex(), EntriesExtractor.ExtractionType.NamedGroupsOneEntry
        ).toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test MinMaxEntriesExtractor with dynamic group names`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "cpu0: 12 cpu1: 34 cpu3: 66"
        )
        val pattern = """(.*):\s(\d+)\s(.*):\s(\d+)\s(.*):\s(\d+)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "cpu0", 12f),
            TimeLineFloatEntry(1234567890L, "cpu1", 34f),
            TimeLineFloatEntry(1234567890L, "cpu3", 66f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries
        ).toSet()
        assertEquals(expected, actual)
    }

}