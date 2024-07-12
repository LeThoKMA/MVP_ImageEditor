package com.example.imageEditor.custom

import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.abs

class ImageListener :
    GestureDetector.SimpleOnGestureListener() {
    private var imageView: ImageView? = null

    fun addListenerOnImageView(imageView: ImageView) {
        this.imageView = imageView
    }

    fun removeListener() {
        imageView = null
    }

    fun haveListener() = imageView != null

    private var isZoomIn = false
        set(value) {
            field = value
            imageView?.let {
                if (isZoomIn) {
                    it.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300).start()
                } else {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
                    it.translationX = 0f
                    it.translationY = 0f
                }
            }
        }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (isZoomIn) {
            imageView?.let {
                if (abs(it.translationX - distanceX) <= it.width / 2) it.translationX -= distanceX
                if (abs(it.translationY - distanceY) <= it.height / 2) it.translationY -= distanceY
            }
        }
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        isZoomIn = !isZoomIn
        return true
    }
}
