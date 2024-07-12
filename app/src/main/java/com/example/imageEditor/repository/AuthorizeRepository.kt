package com.example.imageEditor.repository

import com.example.imageEditor.apiService.ApiImpl
import com.example.imageEditor.apiService.NetworkService
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.request.AuthorizeRequest
import com.example.imageEditor.model.response.AuthorizeResponse

class AuthorizeRepository(onListenProcess: OnListenProcess) {
    private val networkService = NetworkService.getInstance(ApiImpl.getInstance(onListenProcess))

    fun authorize(
        body: AuthorizeRequest,
        onSuccess: (AuthorizeResponse) -> Unit,
    ) {
        networkService.authorize(body, onSuccess = { onSuccess(it) })
    }

    companion object {
        private var instance: AuthorizeRepository? = null

        @JvmStatic
        fun getInstance(onListenProcess: OnListenProcess): AuthorizeRepository {
            if (instance == null) {
                instance = AuthorizeRepository(onListenProcess)
            }
            return instance as AuthorizeRepository
        }
    }
}
