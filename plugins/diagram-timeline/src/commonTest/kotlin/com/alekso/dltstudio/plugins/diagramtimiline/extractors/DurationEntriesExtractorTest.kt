package com.alekso.dltstudio.plugins.diagramtimiline.extractors

import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineDurationEntry
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.DurationEntriesExtractor
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import org.junit.Test
import kotlin.test.assertEquals

class DurationEntriesExtractorTest {

    private val extractor = DurationEntriesExtractor()

    @Test
    fun `Test DurationEntriesExtractor using named groups`() {
        val dltMessage = SampleData.create(
            timeStampUs = 1234567890L, payloadText = "TestAppActivity.onStart"
        )
        val dltMessage2 = SampleData.create(
            timeStampUs = 1234567895L, payloadText = "TestAppActivity.onStop"
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