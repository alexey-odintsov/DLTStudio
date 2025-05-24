package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.PercentageEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntry
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineFloatEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.PercentageEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class PercentageEntriesExtractorTest {

    private val extractor = PercentageEntriesExtractor()

    @Test
    fun `Test PercentageEntriesExtractor named groups many entries`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "12% 67% 89%"
        )
        val pattern = """(?<g1>\d+)%\s(?<g2>\d+)%\s(?<g3>\d+)%"""

        val chartData = PercentageChartData()
        chartData.addEntry(StringKey("g1"), PercentageEntry(1234567890L, 12f, null))
        chartData.addEntry(StringKey("g2"), PercentageEntry(1234567890L, 67f, null))
        chartData.addEntry(StringKey("g3"), PercentageEntry(1234567890L, 89f, null))
        val expected = chartData.getKeys().toSet()

        val actual = PercentageChartData()
        extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
            actual
        )
        assertEquals(expected, actual.getKeys().toSet())
    }

    @Test
    fun `Test PercentageEntriesExtractor named groups one entry, empty key`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "GPU Load: 5.18%, Preemptions: 39"
        )
        val pattern = """GPU Load:\s+(?<value>\d+.\d+)%(?<key>)"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineFloatEntry(1234567890L, "", 5.18f),
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            (entries as PercentageChartData)
        ).toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test PercentageEntriesExtractor with dynamic group names`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "cpu0: 12% cpu1: 34% cpu3: 66%"
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
            EntriesExtractor.ExtractionType.GroupsManyEntries,
            (entries as PercentageChartData)
        ).toSet()
        assertEquals(expected, actual)
    }

}