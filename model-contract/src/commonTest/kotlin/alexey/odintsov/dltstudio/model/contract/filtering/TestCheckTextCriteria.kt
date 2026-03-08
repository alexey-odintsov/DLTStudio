package alexey.odintsov.dltstudio.model.contract.filtering

import org.junit.Assert
import org.junit.Test

class TestCheckTextCriteria {
    @Test
    fun `Test CheckTextCriteria success`() {
        Assert.assertTrue(checkTextCriteria(FilterCriteria("test"), "test message"))
    }

    @Test
    fun `Test CheckTextCriteria empty value`() {
        Assert.assertTrue(checkTextCriteria(FilterCriteria(""), "test message"))
    }

    @Test
    fun `Test CheckTextCriteria fail`() {
        Assert.assertFalse(checkTextCriteria(FilterCriteria("test"), "message"))
    }

    @Test
    fun `Test CheckTextCriteria regexp success`() {
        Assert.assertTrue(
            checkTextCriteria(
                FilterCriteria("\\d+", TextCriteria.Regex),
                "test 1234 message"
            )
        )
    }

    @Test
    fun `Test CheckTextCriteria regexp fail`() {
        Assert.assertFalse(
            checkTextCriteria(
                FilterCriteria("\\d+", TextCriteria.Regex),
                "test message"
            )
        )
    }

    @Test
    fun `Test CheckTextCriteria lowerCase success`() {
        Assert.assertTrue(
            checkTextCriteria(
                FilterCriteria("Message", TextCriteria.LowerCase),
                "test 1234 message"
            )
        )
        Assert.assertTrue(
            checkTextCriteria(
                FilterCriteria("message", TextCriteria.LowerCase),
                "test 1234 Message"
            )
        )
    }

    @Test
    fun `Test CheckTextCriteria lowerCase fail`() {
        Assert.assertFalse(
            checkTextCriteria(
                FilterCriteria("Test", TextCriteria.LowerCase),
                "message"
            )
        )
    }
}