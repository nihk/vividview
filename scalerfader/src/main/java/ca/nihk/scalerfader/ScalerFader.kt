package ca.nihk.scalerfader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.children

/**
 * A ViewGroup that animates a stack of two or more children using scaling and fading effects.
 */
open class ScalerFader : FrameLayout, Properties, Reorderable {

    lateinit var manager: Manager

    override var duration = 3_000
    override var scaleStart = 1f
    override var scaleEnd = 1.25f
    override var fadeFractionStart = 0.8f
    override var interpolator: Interpolator = LinearInterpolator()
    override var startImmediately = false
    var isStarted = false
        private set

    override val children: Sequence<View> get() = (this as ViewGroup).children
    override val lastChild: View get() = getChildAt(childCount - 1)
    override val penultimateChild: View get() = getChildAt(childCount - 2)

    constructor(
        context: Context
    ) : super(context) {
        initialize(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initialize(context, attrs)
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
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
    ) {
        manager = ServiceLocator.managerProvider(this)

        attrs ?: return

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

    open fun start() {
        if (childCount < 2) {
            throw RuntimeException("There must be at least two children.")
        }

        if (isStarted) {
            return
        }
        isStarted = true

        manager.start()
    }

    open fun reset() {
        if (!isStarted) {
            return
        }
        isStarted = false

        manager.reset()
    }

    override fun moveToBack(view: View) {
        removeView(view)
        addView(view, 0)
    }

    override fun addToFront(view: View) {
        addView(view)
    }
}