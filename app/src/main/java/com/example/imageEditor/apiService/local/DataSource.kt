package com.example.imageEditor.apiService.local

import com.example.imageEditor.model.QueryModel

interface DataSource {
    fun saveQuery(
        query: String,
        onResult: (List<QueryModel>) -> Unit,
    )

    fun deleteQuery(
        id: Long,
        onResult: (List<QueryModel>) -> Unit,
    )

    fun getAllQuery(onResult: (List<QueryModel>) -> Unit)
}
