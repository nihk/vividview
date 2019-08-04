package ca.nihk.scalerfader

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes

/**
 * A ViewGroup that animates a stack of two or more children using scaling and fading effects.
 */
open class ScalerFader : FrameLayout {

    private val orderAtStart = mutableListOf<View>()

    /**
     * The duration in millis it takes for one View to start its scale animation until the moment it reaches 0f alpha.
     */
    var duration = 3_000
    var scaleStart = 1f
    var scaleEnd = 1.25f
    /**
     * What percent the scale animation should be complete before the fade animation begins and the scale animation
     * for the View underneath also begins.
     */
    var fadeFractionStart = 0.8f
    var interpolator = LinearInterpolator()
    var startImmediately = false
    var isStarted = false
        private set

    constructor(
        context: Context
    ) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initializeAttributes(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeAttributes(context, attrs, defStyleAttr)
    }

    private fun initializeAttributes(
        context: Context,
        attrs: AttributeSet?, @AttrRes defStyleAttr: Int = 0
    ) {
        with(context.obtainStyledAttributes(attrs, R.styleable.ScalerFader, defStyleAttr, 0)) {
            duration = getInteger(R.styleable.ScalerFader_duration, duration)
            scaleStart = getFloat(R.styleable.ScalerFader_scale_start, scaleStart)
            scaleEnd = getFloat(R.styleable.ScalerFader_scale_end, scaleEnd)
            fadeFractionStart = getFloat(R.styleable.ScalerFader_fade_fraction_start, fadeFractionStart)
            startImmediately = getBoolean(R.styleable.ScalerFader_start_immediately, startImmediately)
            recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (startImmediately && !isStarted) {
            start()
        }
    }

    /**
     * Pushes a View to the top of the View stack. This means that the last View pushed will be the first one that
     * the user sees being animated.
     */
    open fun push(view: View) {
        if (isStarted) {
            throw RuntimeException("Don't push Views while the animation is started).")
        }

        addView(view)
    }

    open fun pushAll(vararg views: View) {
        views.forEach(::push)
    }

    open fun start() {
        if (childCount < 2) {
            throw RuntimeException("There must be at least two children.")
        }

        if (isStarted) {
            return
        }
        isStarted = true

        orderAtStart.apply {
            clear()
            repeat(childCount) { index ->
                add(getChildAt(index))
            }
        }

        startAnimators()
    }

    open fun reset() {
        if (!isStarted) {
            return
        }
        isStarted = false

        removeAllViews()

        orderAtStart.forEach {
            disposeAnimators(it)
            resetPropertyStates(it)
            push(it)
        }
    }

    private fun disposeAnimators(view: View) {
        val fader = view.getTag(R.id.scaler_fader_fade_animator) as? ObjectAnimator
        val animatorSet = view.getTag(R.id.scaler_fader_animator_set) as? AnimatorSet

        fader?.run {
            removeAllUpdateListeners()
            removeAllListeners()
        }

        animatorSet?.cancel()

        view.setTag(R.id.scaler_fader_fade_animator, null)
        view.setTag(R.id.scaler_fader_animator_set, null)
    }

    private fun resetPropertyStates(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
        view.alpha = 1f
    }

    private fun startAnimators() {
        val topmostChild = getChildAt(childCount - 1)
        startAnimators(topmostChild)
    }

    private fun startAnimators(view: View) {
        val scaler = createScaleAnimator(view)
        val fader = createFaderAnimator(view)
        addFaderListeners(view, fader)

        val animatorSet = AnimatorSet().apply {
            interpolator = this@ScalerFader.interpolator
            duration = this@ScalerFader.duration.toLong()
            playTogether(scaler, fader)
            start()
        }

        stashAnimators(view, animatorSet, fader)
    }

    private fun createScaleAnimator(view: View): ObjectAnimator {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleStart, scaleEnd)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleStart, scaleEnd)
        return ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
    }

    private fun createFaderAnimator(view: View): ObjectAnimator {
        val idle = Keyframe.ofFloat(0f, 1f)
        val startFade = Keyframe.ofFloat(fadeFractionStart, 1f)
        val endFade = Keyframe.ofFloat(1f, 0f)

        val propertyValuesHolder = PropertyValuesHolder.ofKeyframe(View.ALPHA, idle, startFade, endFade)
        return ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolder)
    }

    private fun addFaderListeners(view: View, fader: ObjectAnimator) {
        fader.addUpdateListener {
            val startFading = it.animatedFraction >= fadeFractionStart
            if (startFading) {
                fader.removeAllUpdateListeners()
                // Start the next View's animators
                val penultimateChild = getChildAt(childCount - 2)
                startAnimators(penultimateChild)
            }
        }

        fader.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                fader.removeAllListeners()
                // Reorder the Views so there will always be a View behind another one that can be faded into.
                removeView(view)
                addView(view, 0)
            }
        })
    }

    /**
     * Keep a handle on the Animators within Views themselves so that they can be cancelled/unlistened to.
     */
    private fun stashAnimators(
        view: View,
        animatorSet: AnimatorSet,
        fader: ObjectAnimator
    ) {
        disposeAnimators(view)
        view.setTag(R.id.scaler_fader_animator_set, animatorSet)
        view.setTag(R.id.scaler_fader_fade_animator, fader)
    }
}