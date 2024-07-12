package com.example.imageEditor.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.utils.colorFilterList
import com.example.imageEditor.utils.displayImageWithBitmap

class BitmapFilterAdapter(
    private val bitmap: Bitmap,
    private val context: Context,
    private val onFilterPicked: OnFilterPicked,
) :
    RecyclerView.Adapter<BitmapFilterAdapter.ViewHolder>() {
    private val filterOptions = colorFilterList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPreview: ImageFilterView = view.findViewById(R.id.imgPreview)

        fun bind(
            colorFilter: ColorFilter,
            bitmap: Bitmap,
            onFilterPicked: OnFilterPicked,
        ) {
            imgPreview.displayImageWithBitmap(bitmap)
            imgPreview.colorFilter = colorFilter
            imgPreview.setOnClickListener { onFilterPicked.filterPicked(colorFilter) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_filter_preview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filterOptions.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(filterOptions[position], bitmap, onFilterPicked)
    }
}
