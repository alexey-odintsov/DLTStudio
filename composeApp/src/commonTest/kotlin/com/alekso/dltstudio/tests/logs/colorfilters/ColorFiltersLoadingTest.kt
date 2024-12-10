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

class ColorFiltersLoadingTest {

    private val filtersManager = ColorFilterManager()

    @Test
    fun `Test loading color filters from string`() {
        val text = """
            [{"name":"Test filter","filters":{"AppId":{"value":"App1","textCriteria":"PlainText"}},"cellStyle":{"backgroundColor":{"value":-71966338867789824},"textColor":{"value":-6504835343974400}},"enabled":true}]
        """.trimIndent()

        val expected = listOf<ColorFilter>(
            ColorFilter(
                name = "Test filter",
                enabled = true,
                filters = mapOf(
                    FilterParameter.AppId to FilterCriteria("App1", TextCriteria.PlainText),
                ),
                cellStyle = CellStyle(
                    backgroundColor = Color(-71966338867789824),
                    textColor = Color(-6504835343974400)
                ),
            )
        ).toSet()

        val actual = filtersManager.parseFilters(text)?.toSet()
        assertEquals(expected, actual)
    }

}