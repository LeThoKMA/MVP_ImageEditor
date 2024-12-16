package com.example.imageEditor.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.Spanned
import android.text.style.DrawableMarginSpan
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.ImageProxy
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap
import androidx.emoji2.text.EmojiCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom

fun setSpanForString(
    text: String,
    drawable: Drawable,
): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        DrawableMarginSpan(drawable, 20),
        0,
        spannableString.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    return spannableString
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun colorFilterList(): List<ColorFilter> {
    val colorFilters = mutableListOf<ColorFilter>()

// Tạo ColorMatrix cho mỗi màu cơ bản và tạo ColorFilter từ ColorMatrix
// Màu đen
    val blackMatrix =
        ColorMatrix().apply {
            setSaturation(0f) // Vô hiệu hóa sắc tố
        }
    colorFilters.add(ColorMatrixColorFilter(blackMatrix))

// Màu đỏ
    val redMatrix =
        ColorMatrix().apply {
            setScale(1f, 0f, 0f, 1f) // Chỉ giữ màu đỏ
        }
    colorFilters.add(ColorMatrixColorFilter(redMatrix))

// Màu xanh lá cây
    val greenMatrix =
        ColorMatrix().apply {
            setScale(0f, 1f, 0f, 1f) // Chỉ giữ màu xanh lá cây
        }
    colorFilters.add(ColorMatrixColorFilter(greenMatrix))

// Màu xanh dương
    val blueMatrix =
        ColorMatrix().apply {
            setScale(0f, 0f, 1f, 1f) // Chỉ giữ màu xanh dương
        }
    colorFilters.add(ColorMatrixColorFilter(blueMatrix))

// Màu trắng
    val whiteMatrix =
        ColorMatrix().apply {
            setSaturation(0f) // Vô hiệu hóa sắc tố
            setScale(1f, 1f, 1f, 1f) // Tất cả thành phần màu giữ nguyên
        }
    colorFilters.add(ColorMatrixColorFilter(whiteMatrix))

// Màu mờ
    val grayscaleMatrix =
        ColorMatrix().apply {
            setSaturation(0f) // Vô hiệu hóa sắc tố
            setScale(0.33f, 0.33f, 0.33f, 1f) // Biến đổi thành màu xám
        }
    colorFilters.add(ColorMatrixColorFilter(grayscaleMatrix))

// Màu âm bản (negative)
    val invertMatrix =
        ColorMatrix().apply {
            set(
                floatArrayOf(
                    -1f,
                    0f,
                    0f,
                    0f,
                    255f,
                    0f,
                    -1f,
                    0f,
                    0f,
                    255f,
                    0f,
                    0f,
                    -1f,
                    0f,
                    255f,
                    0f,
                    0f,
                    0f,
                    1f,
                    0f,
                ),
            )
        }
    colorFilters.add(ColorMatrixColorFilter(invertMatrix))
    return colorFilters
}

fun authorizeUrl(): String {
    return "https://unsplash.com/oauth/authorize" +
        "?client_id=" + ACCESS_KEY +
        "&redirect_uri=" + REDIRECT_URI +
        "&response_type=" + RESPONSE_TYPE +
        "&scope=" + SCOPE
}

fun String.toAuthorizationCode(): String {
    return this.substring(this.indexOf('=') + 1)
}

fun emojiToDrawable(
    emoji: String,
    context: Context,
): Drawable {
    val processedEmoji = EmojiCompat.get().process(emoji)

    // Tạo TextView ẩn để hiển thị emoji
    val textView = TextView(context)
    textView.text = processedEmoji

    // Đảm bảo TextView có kích thước đủ lớn để hiển thị emoji
    textView.measure(
        View.MeasureSpec.UNSPECIFIED,
        View.MeasureSpec.UNSPECIFIED,
    )
    textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)

    // Chụp TextView vào một Bitmap
    val bitmap =
        Bitmap.createBitmap(
            textView.measuredWidth,
            textView.measuredHeight,
            Bitmap.Config.ARGB_8888,
        )
    val canvas = Canvas(bitmap)
    textView.draw(canvas)

    // Tạo Drawable từ Bitmap

    return BitmapDrawable(context.resources, bitmap)
}

fun getEmojiDrawable(
    emoji: ImageView,
    imageView: ImageView,
) {
    val bitmap = imageView.drawToBitmap()

// Tạo một Canvas từ Bitmap
    val canvas = Canvas(bitmap)

// Vẽ Drawable hoặc Bitmap lên Canvas tại vị trí mong muốn
    val drawable = emoji.drawToBitmap().toDrawable(emoji.resources)
    val x = emoji.x.toInt() // Tọa độ X mong muốn
    val y = emoji.y.toInt() // Tọa độ Y mong muốn
    drawable.setBounds(x, y, x + drawable.intrinsicWidth, y + drawable.intrinsicHeight)
    drawable.draw(canvas)

// Đặt Bitmap này làm Drawable cho ImageView
    imageView.setImageDrawable(BitmapDrawable(imageView.resources, bitmap))
}

fun Float.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

fun isVideoFile(context: Context, uri: Uri): Boolean {
    val contentResolver: ContentResolver = context.contentResolver
    val type = contentResolver.getType(uri)
    return type != null && type.startsWith("video");
}

fun optimizeAndConvertImageToByteArray(bitmap: Bitmap): ByteArray? {
    // Kích thước tối đa mong muốn của ảnh
    val maxWidth = 800
    val maxHeight = 800

    // Tính toán kích thước mới dựa trên tỉ lệ khung hình
    var width = bitmap.width
    var height = bitmap.height
    val ratio = width.toFloat() / height
    if (width > maxWidth || height > maxHeight) {
        if (ratio > 1) {
            width = maxWidth
            height = (width / ratio).toInt()
        } else {
            height = maxHeight
            width = (height * ratio).toInt()
        }
    }

    // Thay đổi kích thước ảnh
    val newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

    // Chuyển đổi ảnh thành byte array
    val baos = ByteArrayOutputStream()
    newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
    val byteArray = baos.toByteArray()

    // Giải phóng bộ nhớ của bitmap
    newBitmap.recycle()
    return byteArray
}
fun convertVideoToByteArray(context: Context, videoUri: Uri?): ByteArray? {
    val contentResolver = context.contentResolver
    var inputStream: InputStream? = null
    return try {
        // Mở InputStream từ URI
        inputStream = contentResolver.openInputStream(videoUri!!)

        // Đọc dữ liệu từ InputStream và chuyển đổi thành mảng byte
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        byteArrayOutputStream.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

fun convertUriToBitmap(context: Context, imageUri: Uri?): Bitmap? {
    val contentResolver = context.contentResolver
    var inputStream: InputStream? = null
    return try {
        // Mở InputStream từ URI
        inputStream = contentResolver.openInputStream(imageUri!!)

        // Đọc dữ liệu từ InputStream và chuyển đổi thành đối tượng Bitmap
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

suspend fun getVideoFileSize(uri: Uri, context: Context): Long? {
    return withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        var fileSize: Long? = null
        val cursor = contentResolver.query(uri, null, null, null, null, null)

        cursor?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                fileSize = cursor.getLong(sizeIndex)
            }
        }

        return@withContext fileSize
    }
}

fun generateRandomIV(): ByteArray {
    val secureRandom = SecureRandom()
    val iv = ByteArray(12) // 96 bits IV for GCM
    secureRandom.nextBytes(iv)
    return iv
}
fun ByteArray.byteArrayToString(): String{
    return Base64.encodeToString(this, Base64.DEFAULT)
}
