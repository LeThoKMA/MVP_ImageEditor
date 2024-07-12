package com.example.imageEditor.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.databinding.ItemSearchBinding
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.model.Urls
import com.example.imageEditor.utils.displayImage

class SearchImageAdapter(private val onClickImage: (String) -> Unit) :
    ListAdapter<PhotoModel, SearchImageAdapter.ViewHolder>(
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
    class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            url: Urls,
            onClickImage: (String) -> Unit,
        ) {
            binding.img.displayImage(url.regular)
            binding.img.setOnClickListener {
                onClickImage(url.regular)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            ItemSearchBinding.inflate(
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
        holder.bind(getItem(position).urls, onClickImage)
    }
}
