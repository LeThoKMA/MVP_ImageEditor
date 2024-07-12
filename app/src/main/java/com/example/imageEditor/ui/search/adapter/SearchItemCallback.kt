package com.example.imageEditor.ui.search.adapter

interface SearchItemCallback {
    fun deleteItemQuery(
        id: Long,
        position: Int,
    )

    fun selectQuery(
        query: String,
    )
}
