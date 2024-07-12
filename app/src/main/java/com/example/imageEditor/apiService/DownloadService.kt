package com.example.imageEditor.apiService

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.example.imageEditor.App
import com.example.imageEditor.utils.FILE_TITLE
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class DownloadService() {
    fun downloadImage(url: String) {
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        executorService.submit {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(FILE_TITLE)
            val fileName = "image/${UUID.randomUUID()}.jpg"
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager =
                App.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
    }

    fun saveImage(
        bitmap: Bitmap,
        onDownloading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        onDownloading.invoke()
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: FutureTask<Unit> =
            FutureTask(
                Callable {
                    val fileName = "image/${UUID.randomUUID()}.jpg"
                    val directory =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!directory.exists()) {
                        directory.mkdirs() // Tạo thư mục nếu nó chưa tồn tại
                    }
                    try {
                        val file = File(directory, fileName)
                        val outputStream = FileOutputStream(file)
                        // Nén và ghi bitmap vào tệp
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                        outputStream.flush()
                        outputStream.close()
                        return@Callable
                    } catch (e: IOException) {
                        throw Throwable(e)
                    }
                },
            )
        executorService.submit(futureTask)
        try {
            futureTask.get()
            onSuccess()
        } catch (e: Exception) {
            onError.invoke(Throwable(e))
        } finally {
            executorService.shutdown()
        }
    }

    companion object {
        private var instance: DownloadService? = null

        fun getInstance(): DownloadService {
            if (instance == null) {
                instance = DownloadService()
            }
            return instance as DownloadService
        }
    }
}
