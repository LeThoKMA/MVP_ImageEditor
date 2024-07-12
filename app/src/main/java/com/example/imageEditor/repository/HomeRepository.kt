package com.example.imageEditor.repository

import com.example.imageEditor.apiService.ApiImpl
import com.example.imageEditor.apiService.NetworkService
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.CollectionModel

class HomeRepository(onListenProcess: OnListenProcess) {
    private val networkService = NetworkService.getInstance(ApiImpl.getInstance(onListenProcess))

    fun getCollections(
        page: Int,
        onResult: (List<CollectionModel>?) -> Unit,
    ) {
        networkService.getCollections(page) {
            onResult.invoke(it)
        }
    }

    fun likeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        networkService.likeImage(id, onFailure)
    }

    companion object {
        private var instance: HomeRepository? = null

        @JvmStatic
        fun getInstance(onListenProcess: OnListenProcess): HomeRepository {
            if (instance == null) {
                instance = HomeRepository(onListenProcess)
            }
            return instance as HomeRepository
        }
    }
}
