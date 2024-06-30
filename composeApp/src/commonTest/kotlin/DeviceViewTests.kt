import androidx.compose.ui.geometry.Rect
import com.alekso.dltstudio.logs.infopanel.DeviceView
import org.junit.Assert
import org.junit.Test

class DeviceViewTests {

    @Test
    fun `Test DeviceView parsing`() {
        val text =
            "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832}"
        val expectedText = listOf(
            DeviceView(Rect(2298f, 22f, 2835f, 709f), id = "app:id/theme_container"),
            DeviceView(Rect(0f, 0f, 1f, 1f), id = "app:id/focus_parking_view"),
        )
        val parsed = DeviceView.parse(text)
        Assert.assertTrue(
            "\n$parsed\n!=\n$expectedText",
            parsed == expectedText
        )
    }

    @Test
    fun `Test DeviceView parsing curly brackets`() {
        val text =
            "FocusParkingView[1343]: onGlobalFocusChanged: oldFocus:androidx.appcompat.widget.AppCompatImageView{73a3b5 VFED..C.. ......ID 576,0-696,120 #7f0b0262 app:id/menuIcon}, newFocus:com.android.car.ui.FocusParkingView{5326a41 VFED..... .F...... 0,0-1,1 #7f0b0177 app:id/focus_parking_view}"
        val expectedText = listOf(
            DeviceView(Rect(576f, 0f, 696f, 120f), id = "app:id/menuIcon"),
            DeviceView(Rect(0f, 0f, 1f, 1f), id = "app:id/focus_parking_view"),
        )
        val parsed = DeviceView.parse(text)
        Assert.assertTrue(
            "\n$parsed\n!=\n$expectedText",
            parsed == expectedText
        )
    }

    @Test
    fun `Test DeviceView parsing without id`() {
        val text =
            "pages.views.test{44f6b84 V.E...... ......I. 0,10-10,20} onAttachedToWindow."
        val expectedText = listOf(DeviceView(Rect(0f, 10f, 10f, 20f)))
        val parsed = DeviceView.parse(text)
        Assert.assertTrue(
            "\n$parsed\n!=\n$expectedText",
            parsed == expectedText
        )
    }


}