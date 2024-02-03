import androidx.compose.ui.graphics.Color
import org.junit.Test
import kotlin.system.measureTimeMillis

class UserStateViewPerformanceTest {

    @Test
    fun `test color generating`() {
        val dataClassTime = measureTimeMillis {
            val colors = mutableListOf<Color>()
            repeat(1000) {
                for (j in 0..100) {
                    for (i in 0..100) {
                        colors.add(Color(.4f, .2f, 1f, .5f))
                    }
                }
            }
            println("total colors: ${colors.size}")
        }
        println(dataClassTime)
    }
}