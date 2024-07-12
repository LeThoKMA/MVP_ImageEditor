package com.example.imageEditor.ui.detail

import android.graphics.Bitmap
import com.example.imageEditor.base.BasePresenter

interface DetailContract {
    interface View {
        fun onSaveSuccess()

        fun onSaveError(throwable: Throwable)

        fun onDownloading()
    }

    interface Presenter : BasePresenter<View> {
        fun downloadImage(url: String)

        fun saveImage(bitmap: Bitmap)
    }
}
