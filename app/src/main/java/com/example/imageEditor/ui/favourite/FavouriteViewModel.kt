package com.example.imageEditor.ui.favourite

import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageEditor.cipher.MyKeystore.decryptToBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class FavouriteViewModel : ViewModel() {
    private val _data = MutableLiveData<List<Bitmap>>()
    val data: LiveData<List<Bitmap>> get() = _data

    init {
       // readAllFilesInImageEditorDirectory()
    }

    fun readAllFilesInImageEditorDirectory() {
        viewModelScope.launch {
            // Xác định thư mục "imageEditor" trong thư mục "Downloads"
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "imageEditor"
            )
            val imageBitmaps = mutableListOf<Bitmap>()
            // Kiểm tra xem thư mục có tồn tại hay không
            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles() // Lấy danh sách các file trong thư mục

                if (!files.isNullOrEmpty()) {
                    for (file in files) {
                        if (file.isFile) {
                            try {
                                // Đọc nội dung file dưới dạng byte array
                                val content = file.readBytes()
                                imageBitmaps.add(decryptToBitmap(content))
                                // Nếu cần, xử lý nội dung file tại đây (ví dụ: giải mã hoặc phân tích dữ liệu)
                            } catch (e: IOException) {
                            }
                        } else if (file.isDirectory) {
                            println("Đây là thư mục con: ${file.name}")
                        }
                    }
                    _data.postValue(imageBitmaps)
                } else {
                    println("Thư mục imageEditor không chứa file nào.")
                }
            } else {
                println("Thư mục imageEditor không tồn tại.")
            }
        }
    }
}
