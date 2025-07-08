package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.StateEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateEntriesExtractorTest {

    private val extractor = StateEntriesExtractor()

    @Test
    fun `Test StateEntriesExtractor using named groups`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L,
                payloadText = "User 10 state changed from LOADING to RUNNING"
            )
        )

        val key1 = StringKey("10")

        val actualChartData = StateChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """User\s(?<key>\d+)\sstate changed from (?<oldvalue>.*) to (?<value>.*)""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].oldState == "LOADING")
        assertTrue(actualChartData.getEntries(key1)[0].newState == "RUNNING")
    }

    @Test
    fun `Test StateEntriesExtractor with dynamic group names`() {
        val logMessage = LogMessage(
            SampleData.create(
            timeStampUs = 1234567890L, payloadText = "User 10 state changed from LOADING to RUNNING"
            )
        )

        val key1 = StringKey("10")

        val actualChartData = StateChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """User\s(\d+)\sstate changed from (.*) to (.*)""".toRegex(),
            EntriesExtractor.ExtractionType.GroupsManyEntries,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].oldState == "LOADING")
        assertTrue(actualChartData.getEntries(key1)[0].newState == "RUNNING")
    }

}