package ca.nihk.vividview

import android.view.ViewGroup
import androidx.core.view.children

internal fun ViewGroup.rotateChildrenRight() {
    val view = children.last()
    removeView(view)
    addView(view, 0)
}

internal fun ViewGroup.penultimateChild() = getChildAt(childCount - 2)
