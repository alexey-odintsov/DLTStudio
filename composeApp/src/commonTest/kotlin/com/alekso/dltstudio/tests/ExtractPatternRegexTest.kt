package com.alekso.dltstudio.tests

import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.ExtractorChecker
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import org.junit.Assert
import org.junit.Test

class ExtractPatternRegexTest {

    @Test
    fun `Test Extract patter groups global`() {
        val extractPattern = """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?"""
        val testPayload =
            "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%"
        val expectedText =
            "cpu0 -> 36.9\ncpu1 -> 40.4\ncpu2 -> 40\ncpu3 -> 43.5\ncpu4 -> 45.3\ncpu5 -> 27.9\ncpu6 -> 16.8\ncpu7 -> 14.1"
        Assert.assertTrue(
            "",
            ExtractorChecker.testRegex(
                extractPattern,
                testPayload,
                EntriesExtractor.ExtractionType.GroupsManyEntries,
                DiagramType.Percentage,
                true
            ) == expectedText
        )
    }

    @Test
    fun `Test Extract patter groups non global`() {
        val extractPattern =
            """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?"""
        val testPayload =
            "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%"
        val expectedText =
            "cpu0 -> 36.9\ncpu1 -> 40.4\ncpu2 -> 40\ncpu3 -> 43.5\ncpu4 -> 45.3\ncpu5 -> 27.9\ncpu6 -> 16.8\ncpu7 -> 14.1"
        Assert.assertTrue(
            "",
            ExtractorChecker.testRegex(
                extractPattern,
                testPayload,
                EntriesExtractor.ExtractionType.GroupsManyEntries,
                DiagramType.Percentage,
                false
            ) == expectedText
        )
    }

}