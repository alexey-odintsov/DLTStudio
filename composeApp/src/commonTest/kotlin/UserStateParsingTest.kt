import com.alekso.dltstudio.ui.user.UserAnalyzer
import com.alekso.dltstudio.ui.user.UserState
import com.alekso.dltstudio.ui.user.UserStateEntry
import org.junit.Assert
import org.junit.Test
import kotlin.time.measureTime

class UserStateParsingTest {

    companion object {
        const val SAMPLES_COUNT = 100_000
        const val PAYLOAD =
            "ActivityManager[866]: User 0 state changed from RUNNING_UNLOCKING to RUNNING_UNLOCKED"
        val USER_ENTRY =
            UserStateEntry(1, 1L, 0, UserState.RUNNING_UNLOCKING, UserState.RUNNING_UNLOCKED)
    }

    @Test
    fun `user state parsing using split`() {
        val dataClassTime = measureTime {
            repeat(SAMPLES_COUNT) {
                val userStateEntry = UserAnalyzer.parseUserStateSplit(1, 1L, PAYLOAD)
                Assert.assertTrue(USER_ENTRY == userStateEntry)
            }
        }
        println("Parsing using split took: $dataClassTime")
    }

    @Test
    fun `user state parsing using indexOf`() {
        val dataClassTime = measureTime {
            repeat(SAMPLES_COUNT) {
                val userStateEntry = UserAnalyzer.parseUserStateIndexOf(1, 1L, PAYLOAD)
                Assert.assertTrue(USER_ENTRY == userStateEntry)
            }
        }
        println("Parsing using indexOf took: $dataClassTime")
    }
}