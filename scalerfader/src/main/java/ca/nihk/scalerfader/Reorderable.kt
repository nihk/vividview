package ca.nihk.scalerfader

import android.view.View

interface Reorderable {

    val children: Sequence<View>
    val lastChild: View
    val penultimateChild: View

    fun moveToBack(view: View)
    fun addToFront(view: View)
    fun removeAllViews()
}