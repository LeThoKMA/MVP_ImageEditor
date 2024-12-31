package com.example.imageEditor.apiService

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.imageEditor.App
import com.example.imageEditor.cipher.MyKeystore
import com.example.imageEditor.utils.FILE_TITLE
import com.example.imageEditor.utils.TAG_NAME
import com.example.imageEditor.utils.bitmapToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        CoroutineScope(IO).launch {
            withContext(Main) {
                onDownloading.invoke()
            }
            runCatching {
                val fileName = "${UUID.randomUUID()}.jpg"
                val childName = "imageEditorPublic"
                val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!directory.exists()) {
                    directory.mkdirs() // Tạo thư mục nếu nó chưa tồn tại
                }
                val pathFile = File(directory, childName)
                if (!pathFile.exists()) {
                    pathFile.mkdir()
                }
                val file = File(pathFile, fileName)
                FileOutputStream(file).use { it ->
                    val data = bitmapToByteArray(bitmap)
                    it.write(data)
                }

            }.fold(
                onSuccess = {
                    withContext(Main) {
                        onSuccess.invoke()
                    }
                },
                onFailure = {
                    withContext(Main) { onError.invoke(Throwable(it)) }
                })
        }
    }

    fun saveImageEncrypt(
        bitmap: Bitmap,
        onDownloading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {

        CoroutineScope(IO).launch {
            withContext(Main) {
                onDownloading.invoke()
            }
            runCatching {
                val fileName = "${UUID.randomUUID()}.jpg"
                val childName = "imageEditorPrivate"
                val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!directory.exists()) {
                    directory.mkdirs() // Tạo thư mục nếu nó chưa tồn tại
                }
                val pathFile = File(directory, childName)
                if (!pathFile.exists()) {
                    pathFile.mkdir()
                }
                val file = File(pathFile, fileName)
                FileOutputStream(file).use { it ->
                    val data = MyKeystore.encryptBitmap(bitmap)
                    it.write(data)
                }

            }.fold(
                onSuccess = {
                    withContext(Main) {
                        onSuccess.invoke()
                    }
                },
                onFailure = {
                    withContext(Main) { onError.invoke(Throwable(it)) }
                })
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
