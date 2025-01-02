package com.example.imageEditor.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.DrawableMarginSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap
import androidx.emoji2.text.EmojiCompat
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

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
    EmojiCompat.init(context)
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

fun convertUriToBitmap(context: Context, imageUri: Uri?): Bitmap? {
    val contentResolver = context.contentResolver
    var inputStream: InputStream? = null
    return try {
        // Mở InputStream từ URI
        inputStream = imageUri?.let { contentResolver.openInputStream(it) }

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
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Hoặc JPEG
    return byteArrayOutputStream.toByteArray()
}

fun getCustomExifTag(imagePath: String): Boolean? {
    return try {
        val exif = ExifInterface(imagePath)
        val tagValue = exif.getAttribute(TAG_NAME)
        tagValue.equals("true")
    } catch (e: IOException) {
        e.printStackTrace()
        println("Không thể đọc tag từ ảnh.")
        null
    }
}
