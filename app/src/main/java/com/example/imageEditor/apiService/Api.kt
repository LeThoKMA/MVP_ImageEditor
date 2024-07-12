package com.example.imageEditor.apiService

import com.example.imageEditor.model.CollectionModel
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.model.PhotoSearchModel
import com.example.imageEditor.model.request.AuthorizeRequest
import com.example.imageEditor.model.response.AuthorizeResponse

interface Api {
    fun getCollections(
        page: Int,
        onResult: (List<CollectionModel>?) -> Unit,
    )

    fun searchPhotos(
        page: Int,
        query: String,
        perPage: Int? = 10,
        onResult: (PhotoSearchModel?) -> Unit,
    )

    fun authorize(
        body: AuthorizeRequest,
        onResult: (AuthorizeResponse) -> Unit,
    )

    fun likeImage(
        id: String,
        onFailure: () -> Unit,
    )

    fun dislikeImage(
        id: String,
        onFailure: () -> Unit,
    )

    fun getFavoriteList(
        name: String,
        page: Int,
        onResult: (List<PhotoModel>) -> Unit,
        onFailure: () -> Unit,
    )
}
