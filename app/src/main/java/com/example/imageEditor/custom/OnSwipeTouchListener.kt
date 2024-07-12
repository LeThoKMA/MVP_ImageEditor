package com.example.imageEditor.custom

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class OnSwipeTouchListener(private val imageView: ImageView) : View.OnTouchListener {
    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(
        view: View?,
        event: MotionEvent?,
    ): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = (event.x - lastX) / 2
                val deltaY = (event.y - lastY) / 2
                imageView.translationX += deltaX
                imageView.translationY += deltaY
                lastX = event.x
                lastY = event.y
            }
        }
        view?.performClick()
        return true
    }
}
