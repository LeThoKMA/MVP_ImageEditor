package com.example.imageEditor.apiService.local

import com.example.imageEditor.model.QueryModel

class LocalService(private val dataSource: DataSource) {
    fun saveQuery(
        query: String,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        dataSource.saveQuery(query) {
            onResult.invoke(it)
        }
    }

    fun deleteQuery(
        id: Long,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        dataSource.deleteQuery(id) {
            onResult.invoke(it)
        }
    }

    fun getAllQuery(onResult: (List<QueryModel>) -> Unit) {
        dataSource.getAllQuery(onResult = {
            onResult.invoke(it)
        })
    }

    companion object {
        private var instance: LocalService? = null

        @Synchronized
        fun getInstance(dataSource: DataSource): LocalService {
            if (instance == null) {
                instance = LocalService(dataSource)
            }
            return instance as LocalService
        }
    }
}
