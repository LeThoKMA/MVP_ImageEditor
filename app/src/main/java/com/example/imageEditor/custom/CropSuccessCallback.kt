package com.example.imageEditor.custom

import android.graphics.PointF

interface CropSuccessCallback {
    fun onCropSuccess(
        startPointF: PointF,
        endPointF: PointF,
    )
}
