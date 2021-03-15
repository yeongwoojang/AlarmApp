package com.example.couroutinstudy.util

import android.util.Log
import android.view.animation.Animation
import android.view.animation.Transformation
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class SlidingUpPanelResizeAnimation(val slidingLayout: SlidingUpPanelLayout, val to : Float, val duration : Int) : Animation(){
    private val mFrom : Float
    init {
        setDuration(duration.toLong())
        mFrom = 0F
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val dimension = (to - mFrom) *interpolatedTime + mFrom

        slidingLayout.panelHeight = dimension.toInt()
        slidingLayout.requestLayout()
    }
}