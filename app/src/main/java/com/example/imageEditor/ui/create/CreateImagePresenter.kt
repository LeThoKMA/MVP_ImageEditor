package com.example.imageEditor.ui.create

import android.graphics.Bitmap
import com.example.imageEditor.repository.CreateImageRepository

class CreateImagePresenter(private val createImageRepository: CreateImageRepository) :
    CreateImageContract.Presenter {
    private var mView: CreateImageContract.View? = null

    override fun saveImage(bitmap: Bitmap) {
        createImageRepository.saveImage(
            bitmap,
            onSuccess = { mView?.onSaveSuccess() },
            onError = { mView?.onSaveError(it) },
            onDownloading = { mView?.onDownloading() },
        )
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

    override fun setView(view: CreateImageContract.View?) {
        this.mView = view
    }
}
