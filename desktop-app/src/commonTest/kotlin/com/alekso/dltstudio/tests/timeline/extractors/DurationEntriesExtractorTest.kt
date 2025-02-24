package com.alekso.dltstudio.tests.timeline.extractors

import com.alekso.dltstudio.tests.Utils
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.filters.extractors.DurationEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType
import org.junit.Test
import kotlin.test.assertEquals

class DurationEntriesExtractorTest {

    private val extractor = DurationEntriesExtractor()

    @Test
    fun `Test DurationEntriesExtractor using named groups`() {
        val dltMessage = Utils.dltMessage(
            timeStampUs = 1234567890L, payload = "TestAppActivity.onStart"
        )
        val dltMessage2 = Utils.dltMessage(
            timeStampUs = 1234567895L, payload = "TestAppActivity.onStop"
        )
        val pattern = """(?<key>.*)\.((?<begin>onStart)|(?<end>onStop))"""

        val expected = listOf<TimeLineEntry<*>>(
            TimeLineDurationEntry(1234567890L, "TestAppActivity", Pair("onStart", null)),
            TimeLineDurationEntry(1234567895L, "TestAppActivity", Pair(null, "onStop")),
        ).toSet()

        val actual1 = extractor.extractEntry(
            dltMessage,
            pattern.toRegex(),
            ExtractionType.NamedGroupsOneEntry
        )
        val actual2 = extractor.extractEntry(
            dltMessage2,
            pattern.toRegex(),
            ExtractionType.NamedGroupsOneEntry
        )
        val actual = (actual1 + actual2).toSet()
        assertEquals(expected, actual)
    }

}