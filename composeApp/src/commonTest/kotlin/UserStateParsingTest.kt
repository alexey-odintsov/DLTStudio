import com.alekso.dltstudio.user.UserAnalyzer
import com.alekso.dltstudio.user.UserState
import com.alekso.dltstudio.user.UserStateEntry
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertTrue
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

    @Test
    fun `test regex`() {
        val payload = "3.4 % test.exe pid :3009(cpid:603 node9) UID:4007233"
        val regex = "(?<value>\\d+.\\d+)\\s+%(.*)pid\\s*:(?<key>\\d+)\\(".toRegex()
        val matches = regex.find(payload)!!
        val key: String? = matches.groups["key"]?.value
        val value: String? = matches.groups["value"]?.value
        println("Found: key = $key value = $value")

        assertTrue(regex.containsMatchIn(payload))
    }

}