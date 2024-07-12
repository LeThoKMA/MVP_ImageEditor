package com.example.imageEditor.ui.create

import android.graphics.Bitmap
import com.example.imageEditor.base.BasePresenter

interface CreateImageContract {
    interface View {
        fun onSaveSuccess()

        fun onSaveError(throwable: Throwable)

        fun onDownloading()
    }

    interface Presenter : BasePresenter<View> {
        fun saveImage(bitmap: Bitmap)
    }
}
