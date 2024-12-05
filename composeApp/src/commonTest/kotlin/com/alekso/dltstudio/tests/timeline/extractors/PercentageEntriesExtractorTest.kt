package com.alekso.dltstudio.tests.timeline.extractors

import com.alekso.dltstudio.tests.Utils
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineFloatEntry
import com.alekso.dltstudio.timeline.filters.extractors.PercentageEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class PercentageEntriesExtractorTest {

    private val extractor = PercentageEntriesExtractor()

    @Test
    fun `Test MinMaxGroupsEntriesExtractor using named groups`() {
        val dltMessage = Utils.dltMessage(
            timeStampNano = 1234567890L, payload = "12% 67% 89%"
        )
        val pattern = """(?<g1>\d+)%\s(?<g2>\d+)%\s(?<g3>\d+)%"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "g1", 12f),
            TimeLineFloatEntry(1234567890L, "g2", 67f),
            TimeLineFloatEntry(1234567890L, "g3", 89f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            PercentageEntriesExtractor.MinMaxExtractionType.NAMED_GROUPS
        ).toSet()
        assertEquals(actual, expected)
    }

    @Test
    fun `Test MinMaxGroupsEntriesExtractor with dynamic group names`() {
        val dltMessage = Utils.dltMessage(
            timeStampNano = 1234567890L, payload = "cpu0: 12% cpu1: 34% cpu3: 66%"
        )
        val pattern = """(.*):\s(\d+)%\s(.*):\s(\d+)%\s(.*):\s(\d+)%"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "cpu0", 12f),
            TimeLineFloatEntry(1234567890L, "cpu1", 34f),
            TimeLineFloatEntry(1234567890L, "cpu3", 66f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            PercentageEntriesExtractor.MinMaxExtractionType.GROUPS_KEY_VALUE
        ).toSet()
        assertEquals(actual, expected)
    }

}