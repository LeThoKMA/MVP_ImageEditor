package com.example.imageEditor.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.model.PreviewPhoto
import com.example.imageEditor.utils.displayImage

class ImageAdapter(
    private val context: Context,
    private val images: List<PreviewPhoto>,
    private val onClickImage: OnClickImage,
    private val onHided: () -> Unit,
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.img)
        private val imgLiked: ImageView = view.findViewById(R.id.imgLiked)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            photo: PreviewPhoto,
            onClickImage: OnClickImage,
            onHided: () -> Unit,
        ) {
            img.displayImage(photo.urls.regular)
            val gestureDetector =
                GestureDetector(
                    img.context,
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(event: MotionEvent): Boolean {
                            onClickImage.doubleTapForLikeImage(photo.id)
                            animationForLike(imgLiked) {
                                onHided()
                            }
                            return true
                        }

                        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                            onClickImage.clickImage(photo.urls.full)
                            return super.onSingleTapConfirmed(e)
                        }
                    },
                )

            img.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }

        private fun animationForLike(
            view: View,
            onHided: () -> Unit,
        ) {
            val fadeInAnimation: Animation =
                AnimationUtils.loadAnimation(
                    view.context,
                    R.anim.fade_in,
                )
            val fadeOutAnimation: Animation =
                AnimationUtils.loadAnimation(
                    view.context,
                    R.anim.fade_out,
                )
            view.visibility = View.VISIBLE
            view.startAnimation(fadeInAnimation)
            fadeInAnimation.setAnimationListener(
                object : AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        view.startAnimation(fadeOutAnimation)
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }
                },
            )

            fadeOutAnimation.setAnimationListener(
                object : AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        view.visibility = View.GONE
                        onHided()
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }
                },
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(images[position], onClickImage, onHided)
    }
}
