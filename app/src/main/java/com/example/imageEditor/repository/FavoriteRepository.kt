package com.example.imageEditor.repository

import com.example.imageEditor.apiService.ApiImpl
import com.example.imageEditor.apiService.NetworkService
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.PhotoModel

class FavoriteRepository(onListenProcess: OnListenProcess) {
    private val networkService = NetworkService.getInstance(ApiImpl.getInstance(onListenProcess))

    fun getFavoriteList(
        name: String,
        page: Int,
        onResult: (List<PhotoModel>) -> Unit,
        onFailure: () -> Unit,
    ) {
        networkService.getFavoriteList(name, page, onResult = {
            onResult.invoke(it)
        }, onFailure = onFailure)
    }

    fun likeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        networkService.likeImage(id, onFailure = onFailure)
    }

    fun dislikeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        networkService.dislikeImage(id, onFailure = onFailure)
    }

    companion object {
        private var instance: FavoriteRepository? = null

        @JvmStatic
        fun getInstance(onListenProcess: OnListenProcess): FavoriteRepository {
            if (instance == null) {
                instance = FavoriteRepository(onListenProcess)
            }
            return instance as FavoriteRepository
        }
    }
}
