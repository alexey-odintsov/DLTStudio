package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.MinMaxEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MinMaxEntriesExtractorTest {

    private val extractor = MinMaxEntriesExtractor()

    @Test
    fun `Test MinMaxEntriesExtractor using named groups`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "12345 67 890"
            )
        )

        val key1 = StringKey("g1")
        val key2 = StringKey("g2")
        val key3 = StringKey("g3")

        val actualChartData = MinMaxChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """(?<g1>\d+)\s(?<g2>\d+)\s(?<g3>\d+)""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1, key2, key3), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].value == 12345f)
        assertTrue(actualChartData.getEntries(key2)[0].value == 67f)
        assertTrue(actualChartData.getEntries(key3)[0].value == 890f)
    }

    @Test
    fun `Test MinMaxEntriesExtractor using named groups one entry`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "MaxRSS: 345"
            )
        )

        val key1 = StringKey("MaxRSS")

        val actualChartData = MinMaxChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """(?<key>MaxRSS):\s(?<value>\d+)""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].value == 345f)
    }

    @Test
    fun `Test MinMaxEntriesExtractor with dynamic group names`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "cpu0: 12 cpu1: 34 cpu3: 66"
            )
        )
        val key1 = StringKey("cpu0")
        val key2 = StringKey("cpu1")
        val key3 = StringKey("cpu3")

        val actualChartData = MinMaxChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """(.*):\s(\d+)\s(.*):\s(\d+)\s(.*):\s(\d+)""".toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1, key2, key3), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].value == 12f)
        assertTrue(actualChartData.getEntries(key2)[0].value == 34f)
        assertTrue(actualChartData.getEntries(key3)[0].value == 66f)
    }

}