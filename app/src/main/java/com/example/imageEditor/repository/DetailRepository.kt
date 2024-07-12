package com.example.imageEditor.repository

import android.graphics.Bitmap
import com.example.imageEditor.apiService.DownloadService

class DetailRepository {
    private val downloadService = DownloadService.getInstance()

    fun downloadImage(url: String) {
        downloadService.downloadImage(url)
    }

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
        private var instance: DetailRepository? = null

        @JvmStatic
        fun getInstance(): DetailRepository {
            if (instance == null) {
                instance = DetailRepository()
            }
            return instance as DetailRepository
        }
    }
}
