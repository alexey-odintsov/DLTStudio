package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.SingleStateEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingleStateEntriesExtractorTest {

    private val extractor = SingleStateEntriesExtractor()

    @Test
    fun `Test SingleStateEntriesExtractor using named groups`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "User 10 state changed to RUNNING"
            )
        )
        val key1 = StringKey("10")

        val actualChartData = SingleStateChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """User\s(?<key>\d+)\sstate changed to (?<value>.*)""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].state == "RUNNING")
    }

    @Test
    fun `Test SingleStateEntriesExtractor with dynamic group names`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "User 10 state changed to RUNNING"
            )
        )

        val key1 = StringKey("10")

        val actualChartData = SingleStateChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """User\s(\d+)\sstate changed to (.*)""".toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].state == "RUNNING")
    }

}