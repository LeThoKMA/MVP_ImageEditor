package com.example.imageEditor.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.model.QueryModel
import java.util.Calendar

class SearchTextAdapter(
    private val searchItemCallback: SearchItemCallback,
    private val dataList: MutableList<QueryModel>,
) :
    RecyclerView.Adapter<SearchTextAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvContent: TextView = view.findViewById(R.id.tvContent)
        private val imgDelete: ImageView = view.findViewById(R.id.imgDelete)

        fun bind(
            queryModel: QueryModel,
            onDelete: () -> Unit,
            onSelectQuery: (String) -> Unit,
        ) {
            tvContent.text = queryModel.content
            imgDelete.setOnClickListener {
                onDelete()
            }
            tvContent.setOnClickListener { onSelectQuery(tvContent.text.toString()) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_text_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(
            dataList[position],
            onDelete = {
                searchItemCallback.deleteItemQuery(dataList[position].id, position)
            },
            onSelectQuery = { searchItemCallback.selectQuery(it) },
        )
    }

    fun updateDataList(list: List<QueryModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}
