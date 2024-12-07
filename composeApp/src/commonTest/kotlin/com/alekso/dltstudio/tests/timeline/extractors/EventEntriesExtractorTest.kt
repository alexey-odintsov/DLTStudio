package com.alekso.dltstudio.tests.timeline.extractors

import com.alekso.dltstudio.tests.Utils
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.EventEntriesExtractor
import org.junit.Test
import kotlin.test.assertEquals

class EventEntriesExtractorTest {

    private val extractor = EventEntriesExtractor()

    @Test
    fun `Test EventEntriesExtractor using named groups`() {
        val dltMessage = Utils.dltMessage(
            timeStampNano = 1234567890L, payload = "Crash (JAVA_CRASH) detected. Filename: /data/system/dropbox/system_app_crash@1705410604456.txt. Process: com.myapp (PID: 8275). Exception: java.lang.AbstractMethodError.. Crash ID: JAVA:fc4dc801f9b4bf581e24224e8ed117c2617e083c"
        )
        val pattern = """Crash \((?<value>.*)\) detected.*Process:\s(?<key>.*). Exception: (?<info>.*) Crash ID:"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineEventEntry(
                1234567890L,
                "com.myapp (PID: 8275)",
                TimeLineEvent("JAVA_CRASH", "java.lang.AbstractMethodError..")
            )
        ).toSet()

        val actual = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            EntriesExtractor.ExtractionType.KeyValueNamed
        ).toSet()
        assertEquals(actual, expected)
    }

}