package com.example.imageEditor.ui.favourite.adapter

import android.graphics.Bitmap
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.databinding.ItemFavoriteBinding
import com.example.imageEditor.ui.favourite.ImageData
import com.example.imageEditor.utils.displayImageWithBitmap

class FavoriteAdapter(
    private val onLock: (ImageData) -> Unit,
    private val onUnlock: (ImageData) -> Unit
) :
    ListAdapter<ImageData, FavoriteAdapter.ViewHolder>(
        object :
            DiffUtil.ItemCallback<ImageData>() {
            override fun areItemsTheSame(
                oldItem: ImageData,
                newItem: ImageData,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ImageData,
                newItem: ImageData,
            ): Boolean {
                return oldItem == newItem
            }
        },
    ) {
    private val mImageStateList = SparseBooleanArray()

    class ViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            photo: ImageData,
            onLock: (ImageData) -> Unit,
            onUnlock: (ImageData) -> Unit
        ) {
            photo.bitmap?.let {
                binding.imgFavorite.displayImageWithBitmap(it)
            }
            if (photo.isLocked) {
                binding.imgLocked.visibility = View.VISIBLE
                binding.imgUnLocked.visibility = View.GONE
            } else {
                binding.imgLocked.visibility = View.GONE
                binding.imgUnLocked.visibility = View.VISIBLE
            }
            binding.imgLocked.setOnClickListener {
                onUnlock(photo)
            }
            binding.imgUnLocked.setOnClickListener {
                onLock(photo)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bindView(getItem(position), onLock, onUnlock)
    }

    fun resetStateList() {
        mImageStateList.clear()
    }
}
