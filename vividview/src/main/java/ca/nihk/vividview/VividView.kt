package ca.nihk.vividview

import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.animation.addListener
import androidx.core.content.withStyledAttributes
import androidx.core.view.children

open class VividView : FrameLayout {
    constructor(context: Context) : super(context) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private var duration = 3_000
    private var fadeFractionStart = 0.8f
    private var scaleStart = 1f
    private var scaleEnd = 1.25f
    private var startImmediately = false

    private val animatorSets = mutableListOf<AnimatorSet>()

    private fun initialize(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
    ) {
        context.withStyledAttributes(attrs, R.styleable.VividView, defStyleAttr) {
            duration = getInteger(R.styleable.VividView_duration, duration)
            scaleStart = getFloat(R.styleable.VividView_scale_start, scaleStart)
            scaleEnd = getFloat(R.styleable.VividView_scale_end, scaleEnd)
            fadeFractionStart = getFloat(R.styleable.VividView_fade_fraction_start, fadeFractionStart)
            startImmediately = getBoolean(R.styleable.VividView_start_immediately, startImmediately)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (startImmediately) start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    open fun start() {
        check(childCount >= 2) { "There must be at least two children." }

        val isStarted = animatorSets.isNotEmpty()
        if (isStarted) return

        start(children.last())
    }

    open fun stop() {
        animatorSets.forEach(AnimatorSet::cancel)
        animatorSets.clear()
    }

    private fun start(target: View) {
        animatorSets += AnimatorSet().apply {
            interpolator = LinearInterpolator()
            duration = this@VividView.duration.toLong()
            playTogether(target.scaleAnimator(), target.fadeAnimator())
            start()
        }
    }

    private fun View.scaleAnimator(): ObjectAnimator {
        return ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat(View.SCALE_X, scaleStart, scaleEnd),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleStart, scaleEnd)
        )
    }

    private fun View.fadeAnimator(): ObjectAnimator {
        val propertyValuesHolder = PropertyValuesHolder.ofKeyframe(
            View.ALPHA,
            Keyframe.ofFloat(0f, 1f), // Idle
            Keyframe.ofFloat(fadeFractionStart, 1f), // Start fade
            Keyframe.ofFloat(1f, 0f) // Fade complete
        )
        val fade = ObjectAnimator.ofPropertyValuesHolder(this, propertyValuesHolder)

        fade.addUpdateListener { valueAnimator ->
            if (valueAnimator.animatedFraction < fadeFractionStart) return@addUpdateListener
            fade.removeAllUpdateListeners()
            start(penultimateChild())
        }

        fade.addListener(
            onEnd = {
                fade.removeAllListeners()
                animatorSets.removeFirst()
                rotateChildrenRight()
            }
        )

        return fade
    }
}
