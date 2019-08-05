package ca.nihk.scalerfader

import android.animation.*
import android.view.View
import androidx.annotation.VisibleForTesting

open class Manager(
    private val reorderable: Reorderable,
    private val properties: Properties
) {

    @VisibleForTesting
    val initialChildrenOrder = mutableListOf<View>()

    open fun start() {
        initialChildrenOrder.apply {
            clear()
            addAll(reorderable.children)
        }

        startAnimators(reorderable.lastChild)
    }

    open fun reset() {
        reorderable.removeAllViews()
        initialChildrenOrder.forEach {
            disposeAnimators(it)
            resetPropertyStates(it)
            reorderable.addToFront(it)
        }
    }

    fun disposeAnimators(view: View) {
        val fader = getStashedFader(view)
        val animatorSet = getStashedAnimatorSet(view)

        fader?.run {
            removeAllUpdateListeners()
            removeAllListeners()
        }

        animatorSet?.cancel()

        removeStashedAnimators(view)
    }

    fun resetPropertyStates(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
        view.alpha = 1f
    }

    fun startAnimators(view: View) {
        val scaler = createScaleAnimator(view)
        val fader = createFaderAnimator(view)
        addFaderListeners(view, fader)

        val animatorSet = AnimatorSet().apply {
            interpolator = properties.interpolator
            duration = properties.duration.toLong()
            playTogether(scaler, fader)
            start()
        }

        stashAnimators(view, fader, animatorSet)
    }

    fun createScaleAnimator(view: View): ValueAnimator {
        with(properties) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleStart, scaleEnd)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleStart, scaleEnd)
            return ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        }
    }

    fun createFaderAnimator(view: View): ValueAnimator {
        val idle = Keyframe.ofFloat(0f, 1f)
        val startFade = Keyframe.ofFloat(properties.fadeFractionStart, 1f)
        val endFade = Keyframe.ofFloat(1f, 0f)

        val propertyValuesHolder = PropertyValuesHolder.ofKeyframe(View.ALPHA, idle, startFade, endFade)
        return ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolder)
    }

    fun addFaderListeners(view: View, fader: ValueAnimator) {
        fader.addUpdateListener {
            val startFading = it.animatedFraction >= properties.fadeFractionStart
            if (startFading) {
                fader.removeAllUpdateListeners()
                // Start the next View's animators
                startAnimators(reorderable.penultimateChild)
            }
        }

        fader.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                fader.removeAllListeners()
                // Reorder the Views so there will always be a View behind another one that can be faded into.
                reorderable.moveToBack(view)
            }
        })
    }

    /**
     * Keep a handle on the Animators within Views themselves so that they can be cancelled/unlistened to.
     */
    fun stashAnimators(
        view: View,
        fader: ValueAnimator,
        animatorSet: Animator
    ) {
        disposeAnimators(view)
        stashFader(view, fader)
        stashAnimatorSet(view, animatorSet)
    }

    fun stashFader(view: View, fader: ValueAnimator?) {
        view.setTag(R.id.scaler_fader_fade_animator, fader)
    }

    fun stashAnimatorSet(view: View, animator: Animator?) {
        view.setTag(R.id.scaler_fader_animator_set, animator)
    }

    fun getStashedFader(view: View) = view.getTag(R.id.scaler_fader_fade_animator) as? ValueAnimator

    fun getStashedAnimatorSet(view: View) = view.getTag(R.id.scaler_fader_animator_set) as? Animator

    fun removeStashedAnimators(view: View) {
        stashFader(view, null)
        stashAnimatorSet(view, null)
    }
}