package ca.nihk.scalerfader

import android.view.animation.Interpolator


interface Properties {
    /**
     * The duration in millis it takes for one View to start its scale animation until the moment it reaches 0f alpha.
     */
    var duration: Int
    var scaleStart: Float
    var scaleEnd: Float
    /**
     * What percent the scale animation should be complete before the fade animation begins and the scale animation
     * for the View underneath also begins.
     */
    var fadeFractionStart: Float
    var interpolator: Interpolator
    var startImmediately: Boolean
}