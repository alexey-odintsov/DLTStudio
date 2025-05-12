package com.alekso.dltstudio.plugins.diagramtimiline.filters

import com.alekso.dltstudio.model.contract.filtering.FilterCriteria
import com.alekso.dltstudio.model.contract.filtering.FilterParameter
import com.alekso.dltstudio.model.contract.filtering.TextCriteria
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimeLineFilterManager
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFilter
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import org.junit.Test
import kotlin.test.assertEquals

class TimelineFiltersLoadingTest {

    private val filtersManager = TimeLineFilterManager()

    @Test
    fun `Test loading filters with unsupported extractorType from string`() {
        val text = """
            [{
    "name": "User state",
    "enabled": true,
    "filters": {
      "AppId": {
        "value": "ALD",
        "textCriteria": "PlainText"
      },
      "ContextId": {
        "value": "SYST",
        "textCriteria": "PlainText"
      }
    },
    "extractPattern": "User\\s(\\d+)\\sstate changed from (.*) to (.*)",
    "diagramType": "State",
    "extractorType": "KeyValueGroups"
  }]
        """.trimIndent()

        val expected = listOf<TimelineFilter>(
            TimelineFilter(
                name = "User state",
                enabled = true,
                filters = mapOf(
                    FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
                    FilterParameter.ContextId to FilterCriteria("SYST", TextCriteria.PlainText),
                ),
                extractPattern = "User\\s(\\d+)\\sstate changed from (.*) to (.*)",
                diagramType = DiagramType.State,
                extractorType = ExtractionType.GroupsManyEntries
            )
        ).toSet()

        val actual = filtersManager.parseFilters(text)?.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `Test loading filters from string`() {
        val text = """
            [{
    "name": "User state",
    "enabled": true,
    "filters": {
      "AppId": {
        "value": "ALD",
        "textCriteria": "PlainText"
      },
      "ContextId": {
        "value": "SYST",
        "textCriteria": "PlainText"
      }
    },
    "extractPattern": "User\\s(\\d+)\\sstate changed from (.*) to (.*)",
    "diagramType": "State",
    "extractorType": "GroupsManyEntries"
  }]
        """.trimIndent()

        val expected = listOf<TimelineFilter>(
            TimelineFilter(
                name = "User state",
                enabled = true,
                filters = mapOf(
                    FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
                    FilterParameter.ContextId to FilterCriteria("SYST", TextCriteria.PlainText),
                ),
                extractPattern = "User\\s(\\d+)\\sstate changed from (.*) to (.*)",
                diagramType = DiagramType.State,
                extractorType = ExtractionType.GroupsManyEntries,
            )
        ).toSet()

        val actual = filtersManager.parseFilters(text)?.toSet()
        assertEquals(expected, actual)
    }

}