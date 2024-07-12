package com.example.imageEditor.apiService

import com.example.imageEditor.model.CollectionModel
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.model.PhotoSearchModel
import com.example.imageEditor.model.request.AuthorizeRequest
import com.example.imageEditor.model.response.AuthorizeResponse

class NetworkService(private val api: Api) {
    fun getCollections(
        page: Int,
        onResult: (List<CollectionModel>?) -> Unit,
    ) {
        api.getCollections(page) {
            onResult.invoke(it)
        }
    }

    fun searchPhotos(
        page: Int,
        query: String,
        onResult: (PhotoSearchModel?) -> Unit,
    ) {
        api.searchPhotos(page, query, onResult = onResult)
    }

    fun authorize(
        body: AuthorizeRequest,
        onSuccess: (AuthorizeResponse) -> Unit,
    ) {
        api.authorize(body) {
            onSuccess(it)
        }
    }

    fun likeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        api.likeImage(id, onFailure)
    }

    fun dislikeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        api.dislikeImage(id, onFailure)
    }

    fun getFavoriteList(
        name: String,
        page: Int,
        onResult: (List<PhotoModel>) -> Unit,
        onFailure: () -> Unit,
    ) {
        api.getFavoriteList(name, page, onResult = { onResult(it) }, onFailure = onFailure)
    }

    companion object {
        private var instance: NetworkService? = null

        fun getInstance(api: Api): NetworkService {
            if (instance == null) {
                instance = NetworkService(api)
            }
            return instance as NetworkService
        }
    }
}
