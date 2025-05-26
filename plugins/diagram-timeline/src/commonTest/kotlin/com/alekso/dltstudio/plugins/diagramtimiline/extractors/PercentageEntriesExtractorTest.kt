package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.PercentageEntriesExtractor
import org.junit.Test
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.assertEquals

class PercentageEntriesExtractorTest {

    private val extractor = PercentageEntriesExtractor()

    @Test
    fun `Test PercentageEntriesExtractor named groups many entries`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "12% 67% 89%"
        )
        val key1 = StringKey("g1")
        val key2 = StringKey("g2")
        val key3 = StringKey("g3")

        val actualChartData = PercentageChartData()
        extractor.extractEntry(
            dltMessage,
            """(?<g1>\d+)%\s(?<g2>\d+)%\s(?<g3>\d+)%""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1, key2, key3), actualChartData.getKeys().toSet())
        assertTrue("", actualChartData.getEntries(key1)[0].value == 12f)
        assertTrue("", actualChartData.getEntries(key2)[0].value == 67f)
        assertTrue("", actualChartData.getEntries(key3)[0].value == 89f)
    }

    @Test
    fun `Test PercentageEntriesExtractor named groups one entry, empty key`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "GPU Load: 5.18%, Preemptions: 39"
        )
        val key = StringKey("")

        val actualChartData = PercentageChartData()
        extractor.extractEntry(
            dltMessage,
            """GPU Load:\s+(?<value>\d+.\d+)%(?<key>)""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key), actualChartData.getKeys().toSet())
        assertTrue("", actualChartData.getEntries(key)[0].value == 5.18f)
    }

    @Test
    fun `Test PercentageEntriesExtractor with dynamic group names`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "cpu0: 12% cpu1: 34% cpu3: 66%"
        )
        val key1 = StringKey("cpu0")
        val key2 = StringKey("cpu1")
        val key3 = StringKey("cpu3")

        val actualChartData = PercentageChartData()
        extractor.extractEntry(
            dltMessage,
            """(.*):\s(\d+)%\s(.*):\s(\d+)%\s(.*):\s(\d+)%""".toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1, key2, key3), actualChartData.getKeys().toSet())
        assertTrue("", actualChartData.getEntries(key1)[0].value == 12f)
        assertTrue("", actualChartData.getEntries(key2)[0].value == 34f)
        assertTrue("", actualChartData.getEntries(key3)[0].value == 66f)
    }

}