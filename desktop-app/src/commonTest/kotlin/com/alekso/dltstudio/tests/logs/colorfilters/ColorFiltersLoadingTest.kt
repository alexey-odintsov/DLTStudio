package com.alekso.dltstudio.tests.logs.colorfilters

import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.logs.colorfilters.ColorFilterManager
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorFiltersLoadingTest {

    private val filtersManager = ColorFilterManager()

    @Test
    fun `Test loading Colors from string`() {
        val text = """
            [{"name":"Test filter","filters":{"AppId":{"value":"App1","textCriteria":"PlainText"}},"cellStyle":{"backgroundColor":"FF0000FF","textColor":"FF00FF00"},"enabled":true}]
        """.trimIndent()

        val filter = filtersManager.parseFilters(text)?.get(0)!!
        assertEquals("Test filter", filter.name)
        assertEquals(true, filter.enabled)
        assertEquals(Color.Green, filter.cellStyle.textColor)
        assertEquals(Color.Blue, filter.cellStyle.backgroundColor)
        assertEquals(FilterCriteria("App1", TextCriteria.PlainText), filter.filters[FilterParameter.AppId])
    }

    @Test
    fun `Test saving color filters to string`() {
        val filters = listOf(
            ColorFilter(
                name = "Test filter",
                enabled = true,
                filters = mapOf(
                    FilterParameter.AppId to FilterCriteria("App1", TextCriteria.PlainText),
                ),
                cellStyle = CellStyle(
                    backgroundColor = Color.Green,
                    textColor = Color.Blue
                ),
            )
        )

        val actual = filtersManager.saveFilters(filters).replace(" ", "")
        assertTrue(actual.contains("\"backgroundColor\":\"ff00ff00\""))
        assertTrue(actual.contains("\"textColor\":\"ff0000ff\""))
    }

}