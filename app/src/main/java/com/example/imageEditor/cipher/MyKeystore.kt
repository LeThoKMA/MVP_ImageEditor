package com.example.imageEditor.cipher

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.imageEditor.utils.bitmapToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object MyKeystore {
    const val ALIAS = "MyAESKey"
    private val ioDispatcher = Dispatchers.IO
    fun createAESKey() {
        CoroutineScope(ioDispatcher).launch {
            val alias = ALIAS
            if (isKeyExists(alias)) {
                return@launch
            }
            // Tạo một đối tượng KeyGenerator để tạo khóa mã hóa AES, sử dụng "AndroidKeyStore" làm backend lưu trữ an toàn
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            // Xây dựng cấu hình (KeyGenParameterSpec) để thiết lập các thuộc tính cho khóa
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                alias, // Tên định danh (alias) duy nhất cho khóa, dùng để tham chiếu trong Android Keystore
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT // Khóa sẽ được sử dụng cho cả mã hóa và giải mã
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM) // Đặt chế độ hoạt động là GCM (Galois/Counter Mode) cho AES
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // Không sử dụng padding, phù hợp với GCM mode
                .setRandomizedEncryptionRequired(true) // Yêu cầu mã hóa phải ngẫu nhiên (không cho phép giá trị khởi tạo IV lặp lại)
                .build() // Hoàn thành việc xây dựng cấu hình

            // Khởi tạo đối tượng KeyGenerator với cấu hình KeyGenParameterSpec
            keyGenerator.init(keyGenParameterSpec)

            // Tạo khóa AES mới dựa trên cấu hình và lưu nó vào Android Keystore
            keyGenerator.generateKey()
        }
    }

    private fun getAESKey(): SecretKey {
        val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(ALIAS, null) as SecretKey
    }

    fun encryptBitmap(bitmap: Bitmap): ByteArray {
        val data = bitmapToByteArray(bitmap)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Tạo IV (Initialization Vector)
        cipher.init(Cipher.ENCRYPT_MODE, getAESKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)

        // Gắn IV vào đầu dữ liệu đã mã hóa để giải mã sau vì mỗi iv là ngẫu nhiên
        return iv + encryptedData
    }

    suspend fun decryptToBitmap(encryptedData: ByteArray) = withContext(IO) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        // Tách IV (12 byte đầu)
        val iv = encryptedData.copyOfRange(0, 12)
        val encryptedBytes = encryptedData.copyOfRange(12, encryptedData.size)
        val ivSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, getAESKey(), ivSpec)

        val originalData = cipher.doFinal(encryptedBytes)

        // Chuyển byte array về Bitmap
        BitmapFactory.decodeByteArray(originalData, 0, originalData.size)
    }


    private fun isKeyExists(alias: String): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.containsAlias(alias)
    }
}
