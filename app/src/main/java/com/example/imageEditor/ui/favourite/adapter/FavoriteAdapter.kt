package com.example.imageEditor.ui.favourite.adapter

import android.graphics.Bitmap
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.databinding.ItemFavoriteBinding
import com.example.imageEditor.utils.displayImageWithBitmap

class FavoriteAdapter(private val onClickImage: OnClickImage) :
    ListAdapter<Bitmap, FavoriteAdapter.ViewHolder>(
        object :
            DiffUtil.ItemCallback<Bitmap>() {
            override fun areItemsTheSame(
                oldItem: Bitmap,
                newItem: Bitmap,
            ): Boolean {
                return oldItem.sameAs(newItem)
            }

            override fun areContentsTheSame(
                oldItem: Bitmap,
                newItem: Bitmap,
            ): Boolean {
                return oldItem.sameAs(newItem)
            }
        },
    ) {
    private val mImageStateList = SparseBooleanArray()

    class ViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            photo: Bitmap,
            onClickImage: OnClickImage,
            mImageStateList: SparseBooleanArray,
            position: Int,
        ) {
            binding.imgFavorite.displayImageWithBitmap(photo)

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
        holder.bindView(getItem(position), onClickImage, mImageStateList, position)
    }

    fun resetStateList() {
        mImageStateList.clear()
    }
}
