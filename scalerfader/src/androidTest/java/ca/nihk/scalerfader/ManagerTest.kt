package ca.nihk.scalerfader

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ManagerTest {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun resetsWithCorrectOrderAndViewStates() {
        val fakeId = 1234
        val view1 = View(context)
        val view2 = View(context)
        val scalerFader = ScalerFader(context).apply {
            addView(view1)
            addView(view2)
            id = fakeId
        }
        val manager = Manager(scalerFader, scalerFader)
        manager.initialChildrenOrder.apply {
            add(view2)
            add(view1)
        }

        manager.reset()

        Assert.assertEquals(view2, scalerFader.getChildAt(0))
        Assert.assertEquals(view1, scalerFader.getChildAt(1))
    }

    @Test
    fun viewTagsHaveValuesAfterStashingAnimators() {
        val fader = mock(ValueAnimator::class.java)
        val animator = mock(Animator::class.java)
        val view = View(context)
        val scalerFader = mock(ScalerFader::class.java)
        val manager = Manager(scalerFader, scalerFader)

        manager.stashAnimators(view, fader, animator)

        Assert.assertEquals(fader, manager.getStashedFader(view))
        Assert.assertEquals(animator, manager.getStashedAnimatorSet(view))
    }

    @Test
    fun disposesAnimators() {
        val fader = mock(ValueAnimator::class.java)
        val animator = mock(Animator::class.java)
        val view = View(context)
        val scalerFader = mock(ScalerFader::class.java)
        val manager = Manager(scalerFader, scalerFader)
        manager.stashAnimators(view, fader, animator)

        manager.disposeAnimators(view)

        verify(fader).removeAllUpdateListeners()
        verify(fader).removeAllListeners()
        verify(animator).cancel()
        Assert.assertNull(manager.getStashedFader(view))
        Assert.assertNull(manager.getStashedAnimatorSet(view))
    }
}