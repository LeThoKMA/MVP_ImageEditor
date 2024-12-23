package com.example.imageEditor.custom

import android.content.Context
import android.graphics.ColorFilter
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.utils.colorFilterList
import com.example.imageEditor.utils.displayImage

class FilterAdapter(
    private val url: Any,
    private val context: Context,
    private val onFilterPicked: OnFilterPicked,
) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private val filterOptions = colorFilterList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPreview: ImageFilterView = view.findViewById(R.id.imgPreview)

        fun bind(
            colorFilter: ColorFilter,
            url: Any,
            onFilterPicked: OnFilterPicked,
        ) {
            when (url) {
                is String -> imgPreview.displayImage(Uri.parse(url)) // Chuyển đổi String thành Uri
                is Uri -> imgPreview.displayImage(url)
                else -> throw IllegalArgumentException("Unsupported type for url: $url")
            }
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
        holder.bind(filterOptions[position], url, onFilterPicked)
    }
}
