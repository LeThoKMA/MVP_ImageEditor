package com.example.imageEditor.repository

import android.graphics.Bitmap
import com.example.imageEditor.apiService.DownloadService

class CreateImageRepository {
    private val downloadService = DownloadService.getInstance()

    fun saveImage(
        bitmap: Bitmap,
        onSuccess: () -> Unit,
        onDownloading: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        downloadService.saveImage(
            bitmap,
            onSuccess = { onSuccess() },
            onError = { onError(it) },
            onDownloading = { onDownloading() },
        )
    }

    companion object {
        private var instance: CreateImageRepository? = null

        @JvmStatic
        fun getInstance(): CreateImageRepository {
            if (instance == null) {
                instance = CreateImageRepository()
            }
            return instance as CreateImageRepository
        }
    }
}
