package com.example.imageEditor.ui.favourite.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.core.util.set
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.databinding.ItemFavoriteBinding
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.utils.displayImage

class FavoriteAdapter(private val onClickImage: OnClickImage) :
    ListAdapter<PhotoModel, FavoriteAdapter.ViewHolder>(
        object :
            DiffUtil.ItemCallback<PhotoModel>() {
            override fun areItemsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel,
            ): Boolean {
                return oldItem == newItem
            }
        },
    ) {
    private val mImageStateList = SparseBooleanArray()

    class ViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            photo: PhotoModel,
            onClickImage: OnClickImage,
            mImageStateList: SparseBooleanArray,
            position: Int,
        ) {
            binding.imgFavorite.displayImage(photo.urls.regular)
            binding.tvUserName.text = photo.user.username
            binding.tvLocation.text = photo.user.location ?: ""
            binding.imgUser.displayImage(photo.user.profileImage.small)
            binding.tvLikes.text =
                binding.root.context.getString(
                    R.string.liked_by_others,
                    photo.likes.toString(),
                )
            binding.tvDescription.text = photo.description
            if (mImageStateList.containsKey(position)) {
                mImageStateList.get(position).let {
                    if (it) {
                        binding.imgLiked.visibility = View.VISIBLE
                        binding.imgLike.visibility = View.INVISIBLE
                    } else {
                        binding.imgLiked.visibility = View.INVISIBLE
                        binding.imgLike.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.imgLiked.visibility = View.VISIBLE
                binding.imgLike.visibility = View.INVISIBLE
            }
            binding.imgLiked.setOnClickListener {
                onClickImage.dislikeImage(photo.id)
                mImageStateList[position] = false
                binding.imgLiked.visibility = View.INVISIBLE
                binding.imgLike.visibility = View.VISIBLE
            }
            binding.imgLike.setOnClickListener {
                onClickImage.likeImage(photo.id)
                mImageStateList[position] = true
                binding.imgLiked.visibility = View.VISIBLE
                binding.imgLike.visibility = View.INVISIBLE
            }
            binding.imgFavorite.setOnClickListener { onClickImage.clickDetailImage(photo.urls.full) }
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
