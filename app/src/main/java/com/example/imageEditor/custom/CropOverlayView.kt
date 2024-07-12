package com.example.imageEditor.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CropOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var startPoint: PointF
    private lateinit var endPoint: PointF
    private var cropRect: RectF? = null
    private val paint =
        Paint().apply {
            color = Color.TRANSPARENT
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }
    private var mCropSuccessCallback: CropSuccessCallback? = null

    fun setCropSuccessCallback(callback: CropSuccessCallback) {
        mCropSuccessCallback = callback
        paint.color = Color.WHITE
    }

    fun removeCropCallback() {
        mCropSuccessCallback = null
        paint.color = Color.TRANSPARENT
        cropRect = RectF(0f, 0f, 0f, 0f)
        invalidate()
    }

    fun haveInstanceListener() = mCropSuccessCallback != null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            cropRect?.let { drawRect(it, paint) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startPoint = PointF(event.x, event.y)
                endPoint = PointF(event.x, event.y)
                cropRect = RectF(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                endPoint.set(event.x, event.y)
                cropRect?.set(
                    startPoint.x,
                    startPoint.y,
                    endPoint.x,
                    endPoint.y,
                )
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                endPoint.set(event.x, event.y)
                cropRect?.set(
                    startPoint.x,
                    startPoint.y,
                    endPoint.x,
                    endPoint.y,
                )
                invalidate()
                cropRect?.let {
                    if (it.width() > 0 && it.height() > 0) {
                        mCropSuccessCallback?.onCropSuccess(
                            startPoint,
                            endPoint,
                        )
                    }
                }
            }
        }
        return true
    }
}
