package com.alekso.dltstudio.charts

import com.alekso.dltstudio.charts.ui.getSteps
import kotlin.test.Test
import kotlin.test.assertContentEquals


class ChartMathTests {
    @Test
    fun `Test getSteps function`() {
        val expected = listOf("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100")
        val actual = getSteps(0f, 100f, 11)
        assertContentEquals(expected, actual, "expected: $expected\nactual:   $actual\n")
    }
}