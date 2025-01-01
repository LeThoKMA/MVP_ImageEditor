package com.example.imageEditor.ui.favourite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageEditor.apiService.DownloadService
import com.example.imageEditor.cipher.MyKeystore.decryptToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class FavouriteViewModel() : ViewModel() {
    private val _data = MutableLiveData<List<ImageData>>()
    val data: LiveData<List<ImageData>> get() = _data

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
       getData()
    }

    fun getData(){
        viewModelScope.launch {
            merge(
                readAllFilesInImageEditorPublicDirectory(),
                readAllFilesInImageEditorPrivateDirectory()
            ).collect {
                _data.postValue(it)
            }
        }
    }

    private suspend fun readAllFilesInImageEditorPublicDirectory(): Flow<List<ImageData>> =
        withContext(Dispatchers.IO) {
            flow {
                // Xác định thư mục "imageEditor" trong thư mục "Downloads"
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "imageEditorPublic"
                )
                val imageBitmaps = mutableListOf<ImageData>()
                // Kiểm tra xem thư mục có tồn tại hay không
                if (directory.exists() && directory.isDirectory) {
                    val files = directory.listFiles() // Lấy danh sách các file trong thư mục

                    if (!files.isNullOrEmpty()) {
                        for (file in files) {
                            if (file.isFile) {
                                try {
                                    // Đọc nội dung file dưới dạng byte array
                                    val content = file.readBytes()
                                    // Giải mã
                                    val bitmap =
                                        BitmapFactory.decodeByteArray(content, 0, content.size)
                                    imageBitmaps.add(
                                        ImageData(
                                            file.name,
                                            bitmap,
                                            false,
                                            file.absolutePath
                                        )
                                    )

                                } catch (e: IOException) {
                                }
                            } else if (file.isDirectory) {
                                println("Đây là thư mục con: ${file.name}")
                            }
                        }
                    } else {
                        println("Thư mục imageEditor không chứa file nào.")
                    }
                } else {
                    println("Thư mục imageEditor không tồn tại.")
                }
                emit(imageBitmaps)
            }
        }

    private suspend fun readAllFilesInImageEditorPrivateDirectory() = withContext(Dispatchers.IO) {
        flow {
            // Xác định thư mục "imageEditor" trong thư mục "Downloads"
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "imageEditorPrivate"
            )
            val imageBitmaps = mutableListOf<ImageData>()
            // Kiểm tra xem thư mục có tồn tại hay không
            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles() // Lấy danh sách các file trong thư mục

                if (!files.isNullOrEmpty()) {
                    for (file in files) {
                        if (file.isFile) {
                            try {
                                // Đọc nội dung file dưới dạng byte array
                                val content = file.readBytes()
                                // Giải mã
                                imageBitmaps.add(
                                    ImageData(
                                        file.name,
                                        decryptToBitmap(content),
                                        true,
                                        file.absolutePath
                                    )
                                )

                            } catch (e: IOException) {
                            }
                        } else if (file.isDirectory) {
                            println("Đây là thư mục con: ${file.name}")
                        }
                    }
                } else {
                    println("Thư mục imageEditor không chứa file nào.")
                }
            } else {
                println("Thư mục imageEditor không tồn tại.")
            }
            emit(imageBitmaps)
        }
    }

    fun unLockFile(image: ImageData) {
        val downloadService = DownloadService.getInstance()
        viewModelScope.launch {
            deleteFileDirectly(image.path)
            image.bitmap?.let {
                downloadService.saveImage(it, image.name, onDownloading = {
                    _message.postValue("Dang chuyen doi")
                }, onSuccess = {
                    _message.postValue("Thanh cong")
                }, onError = {
                    _message.postValue("That bai")
                })
            }
        }

    }

    fun lockFile(image: ImageData) {
        val downloadService = DownloadService.getInstance()
        viewModelScope.launch {
            deleteFileDirectly(image.path)
            image.bitmap?.let {
                downloadService.saveImageEncrypt(it, image.name, onDownloading = {
                    _message.postValue("Dang chuyen doi")
                }, onSuccess = {
                    _message.postValue("Thanh cong")
                }, onError = {
                    _message.postValue("That bai")
                })
            }
        }
    }

    fun deleteFileDirectly(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            file.delete()
        } else {
            println("File không tồn tại")
            false
        }
    }
}

data class ImageData(
    val name: String,
    val bitmap: Bitmap? = null,
    val isLocked: Boolean = false,
    val path: String = "",
)
