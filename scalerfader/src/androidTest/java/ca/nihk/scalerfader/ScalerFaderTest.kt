package ca.nihk.scalerfader

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class ScalerFaderTest {

    lateinit var context: Context
    val manager = mock(Manager::class.java)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        ServiceLocator.managerProvider = { manager }
    }

    @Test
    fun sufficientChildrenStarts() {
        val fakeId = 1234
        val view1 = View(context)
        val view2 = View(context)
        val scalerFader = ScalerFader(context).apply {
            addView(view1)
            addView(view2)
            id = fakeId
        }
        ScalerFaderProvider.scalerFader = scalerFader

        launchActivity<ScalerFaderActivity>().onActivity {
            val sf: ScalerFader = it.findViewById(fakeId)
            sf.start()
            verify(manager).start()
            Assert.assertTrue(sf.isStarted)
        }
    }

    @Test
    fun startImmediatelyStartsImmediately() {
        val fakeId = 1234
        val view1 = View(context)
        val view2 = View(context)
        val scalerFader = ScalerFader(context).apply {
            addView(view1)
            addView(view2)
            id = fakeId
            startImmediately = true
        }
        ScalerFaderProvider.scalerFader = scalerFader

        launchActivity<ScalerFaderActivity>().onActivity {
            val sf: ScalerFader = it.findViewById(fakeId)
            verify(manager).start()
            Assert.assertTrue(sf.isStarted)
        }
    }

    @Test(expected = RuntimeException::class)
    fun insufficientChildrenThrows() {
        val fakeId = 1234
        val view = View(context)
        val scalerFader = ScalerFader(context).apply {
            addView(view)
            id = fakeId
        }
        ScalerFaderProvider.scalerFader = scalerFader

        launchActivity<ScalerFaderActivity>().onActivity {
            val sf: ScalerFader = it.findViewById(fakeId)
            sf.start()
            verify(manager, times(0)).start()
        }
    }
}