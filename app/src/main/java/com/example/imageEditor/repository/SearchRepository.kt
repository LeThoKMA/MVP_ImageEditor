package com.example.imageEditor.repository

import com.example.imageEditor.App
import com.example.imageEditor.apiService.ApiImpl
import com.example.imageEditor.apiService.NetworkService
import com.example.imageEditor.apiService.local.DataSourceImpl
import com.example.imageEditor.apiService.local.LocalService
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.PhotoSearchModel
import com.example.imageEditor.model.QueryModel

class SearchRepository(onListenProcess: OnListenProcess) {
    private val networkService = NetworkService.getInstance(ApiImpl.getInstance(onListenProcess))
    private val localService =
        LocalService.getInstance(DataSourceImpl.getInstance(App.instance, onListenProcess))

    fun searchPhotos(
        page: Int,
        query: String,
        onResult: (PhotoSearchModel?) -> Unit,
    ) {
        networkService.searchPhotos(page, query, onResult)
    }

    fun saveQueryToLocal(
        query: String,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        localService.saveQuery(query) {
            onResult.invoke(it)
        }
    }

    fun deleteQuery(
        id: Long,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        localService.deleteQuery(id, onResult)
    }

    fun getAllQuery(onResult: (List<QueryModel>) -> Unit) {
        localService.getAllQuery(onResult = {
            onResult.invoke(it)
        })
    }

    companion object {
        private var instance: SearchRepository? = null

        @JvmStatic
        fun getInstance(onListenProcess: OnListenProcess): SearchRepository {
            if (instance == null) {
                instance = SearchRepository(onListenProcess)
            }
            return instance as SearchRepository
        }
    }
}
