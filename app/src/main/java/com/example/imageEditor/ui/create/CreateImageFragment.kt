package com.example.imageEditor.ui.create

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.example.imageEditor.R
import com.example.imageEditor.base.BaseFragment
import com.example.imageEditor.custom.BitmapFilterAdapter
import com.example.imageEditor.custom.CropSuccessCallback
import com.example.imageEditor.custom.ImageListener
import com.example.imageEditor.custom.OnFilterPicked
import com.example.imageEditor.custom.OnSwipeTouchListener
import com.example.imageEditor.databinding.FragmentCreateBinding
import com.example.imageEditor.repository.CreateImageRepository
import com.example.imageEditor.utils.DEFAULT_EMOJI_SIZE
import com.example.imageEditor.utils.DEFAULT_PROGRESS_VALUE
import com.example.imageEditor.utils.RANGE_CONTRAST_AND_BRIGHTNESS
import com.example.imageEditor.utils.displayImageWithBitmap
import com.example.imageEditor.utils.dpToPx
import com.example.imageEditor.utils.emojiToDrawable
import com.example.imageEditor.utils.getEmojiDrawable
import com.example.imageEditor.utils.imageProxyToBitmap

class CreateImageFragment :
    BaseFragment<FragmentCreateBinding>(),
    CreateImageContract.View,
    CropSuccessCallback,
    OnFilterPicked {
    private var mImageCapture: ImageCapture? = null
    private val mPresenter by lazy { CreateImagePresenter(CreateImageRepository.getInstance()) }

    private val mImageListener by lazy { binding?.imgCapture?.let { ImageListener() } }
    private var mScaleGestureDetector: GestureDetector? = null

    private val mPath = Path()

    private var mCanvas: Canvas? = null
    private var mMutableBitmap: Bitmap? = null
        set(value) {
            field = value
            mCanvas = value?.let { Canvas(it) }
        }

    private var mPaint =
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

    private var mLastTouchX = 0f
    private var mLastTouchY: Float = 0f

    private var croppedBitmap: Bitmap? = null
    private var bitmapCapture: Bitmap? = null
    private var mFlashMode = false
        set(value) {
            field = value
            if (value) {
                mImageCapture?.flashMode = FLASH_MODE_ON
            } else {
                mImageCapture?.flashMode = FLASH_MODE_OFF
            }
        }

    private var isFrontCamera = false
    private var isFiltering = false
    private val mListEmoji = mutableListOf<ImageView>()
    private lateinit var filterAdapter: BitmapFilterAdapter
    private lateinit var emoji: String

    override fun getViewBinding(inflater: LayoutInflater): FragmentCreateBinding {
        return FragmentCreateBinding.inflate(inflater)
    }

    override fun initView() {
        mPresenter.setView(this)
        binding?.sbContrast?.progress = DEFAULT_PROGRESS_VALUE
        binding?.sbBrightness?.progress = DEFAULT_PROGRESS_VALUE
    }

    override fun initData() {
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        mScaleGestureDetector?.setOnDoubleTapListener(mImageListener)
        drawListener()
        binding?.apply {
            mImageListener?.addListenerOnImageView(imgCapture)
            btCapture.setOnClickListener {
                takePhoto()
            }
            btCapture.setOnClickListener {
                takePhoto()
            }
            imgBack.setOnClickListener {
                resetView()
            }
            imgDownLoad.setOnClickListener {
                mListEmoji.forEach {
                    getEmojiDrawable(it, imgCapture)
                }
                binding?.imgCapture?.drawToBitmap(Bitmap.Config.ARGB_8888)?.let {
                    mPresenter.saveImage(it)
                }
            }
            imgCrop.setOnClickListener {
                cropView.visibility = View.VISIBLE
                cropView.setCropSuccessCallback(this@CreateImageFragment)
                constrainConfirm.visibility = View.VISIBLE
                constrainOption.visibility = View.GONE
            }
            imgCancel.setOnClickListener {
                mImageListener?.addListenerOnImageView(imgCapture)
                isFiltering = false
                bitmapCapture?.let {
                    imgCapture.setImageBitmap(it)
                    mMutableBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
                }
                imgCapture.clearColorFilter()
                hideAndShowElement()
                cropView.removeCropCallback()
                sbContrast.progress = DEFAULT_PROGRESS_VALUE
                sbBrightness.progress = DEFAULT_PROGRESS_VALUE
            }
            imgDone.setOnClickListener {
                if (cropView.haveInstanceListener()) {
                    croppedBitmap?.let { it1 ->
                        imgCapture.setImageBitmap(it1)
                        mMutableBitmap = it1.copy(Bitmap.Config.ARGB_8888, true)
                    }
                    cropView.removeCropCallback()
                    cropView.visibility = View.GONE
                }
                if (isFiltering) {
                    imgCapture.displayImageWithBitmap(imgCapture.drawToBitmap()) {
                        mMutableBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
                    }
                    sbContrast.progress = DEFAULT_PROGRESS_VALUE
                    sbBrightness.progress = DEFAULT_PROGRESS_VALUE
                    isFiltering = false
                }
                if (imgEmojiPreview.visibility == View.VISIBLE) {
                    if (emoji.isNotBlank()) {
                        val layoutParams =
                            ConstraintLayout.LayoutParams(
                                DEFAULT_EMOJI_SIZE.dpToPx(requireContext()),
                                DEFAULT_EMOJI_SIZE.dpToPx(requireContext()),
                            )
                        layoutParams.startToStart = R.id.imgCapture
                        layoutParams.topToTop = R.id.imgCapture
                        layoutParams.endToEnd = R.id.imgCapture
                        layoutParams.bottomToBottom = R.id.imgCapture
                        val imageView =
                            ImageView(requireContext()).apply {
                                this.layoutParams = layoutParams
                                setImageDrawable(emojiToDrawable(emoji, requireContext()))
                                setOnTouchListener(OnSwipeTouchListener(this))
                            }
                        mListEmoji.add(imageView)
                        root.addView(imageView)
                    }
                }
                hideAndShowElement()
                mImageListener?.addListenerOnImageView(imgCapture)
            }
            imgLight.setOnClickListener {
                mFlashMode = !mFlashMode
            }
            imgChangeCamera.setOnClickListener {
                isFrontCamera = !isFrontCamera
                startCamera(isFrontCamera)
            }
            imgFilter.setOnClickListener {
                recycleViewFilterOption.visibility = View.VISIBLE
                constrainConfirm.visibility = View.VISIBLE
                constrainOption.visibility = View.GONE
            }
            imgIcon.setOnClickListener {
                parentEmoji.visibility = View.VISIBLE
                constrainConfirm.visibility = View.VISIBLE
                constrainOption.visibility = View.GONE
                imgEmojiPreview.visibility = View.VISIBLE
            }
            emojiPicker.setOnEmojiPickedListener {
                emoji = it.emoji
                imgEmojiPreview.setImageDrawable(null)
                imgEmojiPreview.setImageDrawable(emojiToDrawable(it.emoji, requireContext()))
            }
        }
        contrastAndBrightness()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawListener() {
        binding?.apply {
            imgDraw.setOnClickListener {
                mImageListener?.removeListener()
                constrainConfirm.visibility = View.VISIBLE
                constrainOption.visibility = View.GONE
                rgColor.visibility = View.VISIBLE
            }

            imgCapture.setOnTouchListener { view, event ->
                if (mImageListener?.haveListener() == true) {
                    mScaleGestureDetector?.onTouchEvent(event)
                } else {
                    val drawable = imgCapture.drawable
                    val imageMatrix = imgCapture.imageMatrix

                    // Tính toán tọa độ tương đối trong Drawable
                    val imageBounds = drawable.bounds
                    val intrinsicWidth = drawable.intrinsicWidth
                    val intrinsicHeight = drawable.intrinsicHeight

                    // Lấy ra ma trận hiển thị của Drawable
                    val displayMatrix = Matrix()
                    imageMatrix.invert(displayMatrix)

                    val touchPoint = floatArrayOf(event.x, event.y)
                    displayMatrix.mapPoints(touchPoint)

                    val imageX =
                        (touchPoint[0] - imageBounds.left) * (intrinsicWidth / imageBounds.width())
                    val imageY =
                        (touchPoint[1] - imageBounds.top) * (intrinsicHeight / imageBounds.height())
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mLastTouchX = imageX
                            mLastTouchY = imageY
                            mPath.reset()
                            mPath.moveTo(imageX, imageY)
                            rgColor.visibility = View.GONE
                        }

                        MotionEvent.ACTION_MOVE -> {
                            mPath.quadTo(
                                mLastTouchX,
                                mLastTouchY,
                                (imageX + mLastTouchX) / 2,
                                (imageY + mLastTouchY) / 2,
                            )
                            mLastTouchX = imageX
                            mLastTouchY = imageY
                            mCanvas?.drawPath(mPath, mPaint)
                            imgCapture.setImageBitmap(mMutableBitmap)
                        }

                        MotionEvent.ACTION_UP -> {
                            rgColor.visibility = View.VISIBLE
                        }
                    }
                }
                view.performClick()
                true
            }
            rgColor.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbBlack -> mPaint.color = Color.BLACK
                    R.id.rbRed -> mPaint.color = Color.RED
                    R.id.rbBlue -> mPaint.color = Color.BLUE
                    R.id.rbYellow -> mPaint.color = Color.YELLOW
                    R.id.rbOrange -> mPaint.color = requireContext().getColor(R.color.orange)
                    R.id.rbPink -> mPaint.color = requireContext().getColor(R.color.pink)
                    R.id.rbWhite -> mPaint.color = Color.WHITE
                }
            }
        }
    }

    private fun contrastAndBrightness() {
        binding?.apply {
            imgContrast.setOnClickListener {
                constraintContrast.visibility = View.VISIBLE
                constrainConfirm.visibility = View.VISIBLE
                constrainOption.visibility = View.GONE
            }
            sbContrast.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        p0: SeekBar?,
                        value: Int,
                        p2: Boolean,
                    ) {
                        if (value > 0) {
                            val contrast = value.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                            imgCapture.contrast = contrast
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        p0?.let {
                            if (p0.progress > 0) {
                                val contrast = p0.progress.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                                imgCapture.contrast = contrast
                            }
                        }
                    }
                },
            )

            sbBrightness.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        p0: SeekBar?,
                        value: Int,
                        p2: Boolean,
                    ) {
                        if (value > 0) {
                            val brightness = value.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                            imgCapture.brightness = brightness
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        p0?.let {
                            if (p0.progress > 0) {
                                val brightness =
                                    p0.progress.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                                imgCapture.brightness = brightness
                            }
                        }
                    }
                },
            )
        }
    }

    private fun hideAndShowElement() {
        binding?.apply {
            recycleViewFilterOption.visibility = View.GONE
            constraintContrast.visibility = View.GONE
            constrainConfirm.visibility = View.GONE
            constrainOption.visibility = View.VISIBLE
            rgColor.visibility = View.GONE
            cropView.visibility = View.GONE
            parentEmoji.visibility = View.GONE
            imgEmojiPreview.setImageDrawable(null)
            imgEmojiPreview.visibility = View.GONE
        }
    }

    private fun startCamera(isFrontCamera: Boolean = false) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview =
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                    }

            mImageCapture =
                ImageCapture.Builder()
                    .build()
            // Select back camera as a default
            val cameraSelector =
                if (!isFrontCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    mImageCapture,
                )
            } catch (exc: Exception) {
                Log.e(">>>>>", exc.message.toString())
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        mImageCapture?.run {
            takePicture(
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        try {
                            val bitmap = imageProxyToBitmap(image)
                            bitmap?.let {
                                setViewAfterCapture(it)
                            }
                        } catch (ex: Exception) {
                            Log.e(">>>>>>", "Error displaying captured image: ${ex.message}")
                        } finally {
                            // Đóng ImageProxy sau khi sử dụng xong
                            image.close()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                    }
                },
            )
        }
    }

    private fun resetView() {
        isFiltering = false
        binding?.imgCapture?.alpha = 0F
        binding?.viewFinder?.visibility = View.VISIBLE
        binding?.constrainOption?.visibility = View.GONE
        binding?.lnOption?.visibility = View.VISIBLE
        binding?.constraintContrast?.visibility = View.GONE
        binding?.recycleViewFilterOption?.visibility = View.GONE
        binding?.sbContrast?.progress = DEFAULT_PROGRESS_VALUE
        binding?.sbBrightness?.progress = DEFAULT_PROGRESS_VALUE
        if (mListEmoji.isNotEmpty()) {
            mListEmoji.forEach {
                binding?.root?.removeView(it)
            }
            mListEmoji.clear()
        }
    }

    private fun setViewAfterCapture(bitmap: Bitmap) {
        binding?.imgCapture?.alpha = 1F
        binding?.viewFinder?.visibility = View.GONE
        binding?.constrainOption?.visibility = View.VISIBLE
        binding?.lnOption?.visibility = View.GONE
        mMutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmapCapture = bitmap
        binding?.imgCapture?.setImageBitmap(mMutableBitmap)
        filterAdapter = BitmapFilterAdapter(bitmap, requireActivity(), this)
        binding?.recycleViewFilterOption?.adapter = filterAdapter
    }

    override fun onSaveSuccess() {
        Toast.makeText(requireContext(), getString(R.string.save_success), Toast.LENGTH_LONG).show()
        resetView()
    }

    override fun onSaveError(throwable: Throwable) {
        Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_SHORT).show()
        resetView()
    }

    override fun onDownloading() {
        Toast.makeText(requireContext(), getString(R.string.downloading), Toast.LENGTH_SHORT).show()
    }

    override fun onCropSuccess(
        startPointF: PointF,
        endPointF: PointF,
    ) {
        binding?.imgCapture?.let {
            val drawable = it.drawable
            val imageMatrix = binding?.imgCapture?.imageMatrix

            // Tính toán tọa độ tương đối trong Drawable
            val imageBounds = drawable.bounds
            val intrinsicWidth = drawable.intrinsicWidth
            val intrinsicHeight = drawable.intrinsicHeight

            // Lấy ra ma trận hiển thị của Drawable
            val displayMatrix = Matrix()
            imageMatrix?.invert(displayMatrix)

            val startPoint = floatArrayOf(startPointF.x, startPointF.y)
            displayMatrix.mapPoints(startPoint)

            val endPoint = floatArrayOf(endPointF.x, endPointF.y)
            displayMatrix.mapPoints(endPoint)

            val startX =
                (startPoint[0] - imageBounds.left) * (intrinsicWidth / imageBounds.width())
            val startY =
                (startPoint[1] - imageBounds.top) * (intrinsicHeight / imageBounds.height())

            val endX =
                (endPoint[0] - imageBounds.left) * (intrinsicWidth / imageBounds.width())
            val endY =
                (endPoint[1] - imageBounds.top) * (intrinsicHeight / imageBounds.height())
            mMutableBitmap?.let { bitmap ->
                // Tính toán hình chữ nhật cắt (crop rectangle) từ startPointF và endPointF
                val left = minOf(startX, endX)
                val top = minOf(startY, endY)
                val right = maxOf(startX, endX)
                val bottom = maxOf(startY, endY)
                val cropRect = RectF(left, top, right, bottom)

                // Tạo một bitmap mới từ phần đã cắt (crop) của bitmap gốc
                if (cropRect.left > 0 && cropRect.top > 0) {
                    croppedBitmap =
                        Bitmap.createBitmap(
                            bitmap,
                            cropRect.left.toInt(),
                            cropRect.top.toInt(),
                            cropRect.width().toInt(),
                            cropRect.height().toInt(),
                        )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
        mScaleGestureDetector =
            mImageListener?.let {
                GestureDetector(
                    requireContext(),
                    it,
                )
            }
    }

    override fun onPause() {
        super.onPause()
        ProcessCameraProvider.getInstance(requireContext()).get().unbindAll()
    }

    override fun onStop() {
        mImageListener?.removeListener()
        mScaleGestureDetector = null
        super.onStop()
    }

    override fun filterPicked(colorFilter: ColorFilter) {
        isFiltering = true
        binding?.imgCapture?.colorFilter = colorFilter
    }
}
