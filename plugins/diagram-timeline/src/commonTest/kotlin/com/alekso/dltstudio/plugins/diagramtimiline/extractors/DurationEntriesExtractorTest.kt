package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.DurationEntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DurationEntriesExtractorTest {

    private val extractor = DurationEntriesExtractor()

    @Test
    fun `Test DurationEntriesExtractor using named groups`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L, payloadText = "TestAppActivity.onStart"
            )
        )
        val logMessage2 = LogMessage(
            SampleData.create(
                timeStampUs = 1234567895L, payloadText = "TestAppActivity.onStop"
            )
        )

        val pattern = """(?<key>.*)\.((?<begin>onStart)|(?<end>onStop))"""

        val key1 = StringKey("TestAppActivity")

        val actualChartData = DurationChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        extractor.extractEntry(
            logMessage2,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].begin == "onStart")
        assertTrue(actualChartData.getEntries(key1)[1].end == "onStop")
    }
}