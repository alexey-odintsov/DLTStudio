package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EventEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventEntriesExtractorTest {

    private val extractor = EventEntriesExtractor()

    @Test
    fun `Test EventEntriesExtractor using named groups`() {
        val logMessage = LogMessage(
            SampleData.create(
                timeStampUs = 1234567890L,
                payloadText = "Crash (JAVA_CRASH) detected. Filename: /data/system/dropbox/system_app_crash@1705410604456.txt. Process: com.myapp (PID: 8275). Exception: java.lang.AbstractMethodError.. Crash ID: JAVA:fc4dc801f9b4bf581e24224e8ed117c2617e083c"
            )
        )

        val key1 = StringKey("com.myapp (PID: 8275)")

        val actualChartData = EventsChartData<LogMessage>()
        extractor.extractEntry(
            logMessage,
            """Crash \((?<value>.*)\) detected.*Process:\s(?<key>.*). Exception: (?<info>.*) Crash ID:""".toRegex(),
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
            actualChartData
        )
        assertEquals(setOf(key1), actualChartData.getKeys().toSet())
        assertTrue(actualChartData.getEntries(key1)[0].event == "JAVA_CRASH")
    }

}