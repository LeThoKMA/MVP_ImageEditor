package com.example.imageEditor.apiService

import android.util.Log
import com.example.imageEditor.base.CustomFuture
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.CollectionModel
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.model.PhotoSearchModel
import com.example.imageEditor.model.request.AuthorizeRequest
import com.example.imageEditor.model.response.AuthorizeResponse
import com.example.imageEditor.utils.AUTHORIZE_ENDPOINT
import com.example.imageEditor.utils.COLLECTION_ENDPOINT
import com.example.imageEditor.utils.PAGE
import com.example.imageEditor.utils.PER_PAGE
import com.example.imageEditor.utils.PHOTO_SEARCH_ENDPOINT
import com.example.imageEditor.utils.QUERY_SEARCH
import com.example.imageEditor.utils.deleteMethodHttpWithBearToken
import com.example.imageEditor.utils.fromJsonToList
import com.example.imageEditor.utils.getMethodHttp
import com.example.imageEditor.utils.getMethodHttpWithBearToken
import com.example.imageEditor.utils.postMethodHttp
import com.example.imageEditor.utils.postMethodHttpWithBearToken
import com.example.imageEditor.utils.toJson
import com.google.gson.Gson
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class ApiImpl(private val onListenProcess: OnListenProcess) : Api {
    private var mToken: String? = ""
    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    override fun getCollections(
        page: Int,
        onResult: (List<CollectionModel>?) -> Unit,
    ) {
        val futureTask: CustomFuture<List<CollectionModel>> =
            CustomFuture(
                Callable {
                    try {
                        val query = mapOf(Pair(PAGE, page.toString()))
                        return@Callable Gson().fromJsonToList<CollectionModel>(
                            getMethodHttp(COLLECTION_ENDPOINT, query),
                        )
                    } catch (e: Exception) {
                        throw Throwable(e)
                    }
                },
                onListenProcess,
            )
        executorService.submit(futureTask)
        try {
            onResult.invoke(futureTask.get())
        } catch (e: Exception) {
            onListenProcess.onError(Throwable(e))
        }
    }

    override fun searchPhotos(
        page: Int,
        query: String,
        perPage: Int?,
        onResult: (PhotoSearchModel?) -> Unit,
    ) {
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: CustomFuture<PhotoSearchModel> =
            CustomFuture(
                Callable {
                    try {
                        val queryValue =
                            mapOf(
                                Pair(PAGE, page.toString()),
                                Pair(QUERY_SEARCH, query),
                                Pair(
                                    PER_PAGE,
                                    perPage.toString(),
                                ),
                            )
                        return@Callable Gson().fromJson(
                            getMethodHttp(PHOTO_SEARCH_ENDPOINT, queryValue),
                            PhotoSearchModel::class.java,
                        )
                    } catch (e: Exception) {
                        throw Throwable(e)
                    }
                },
                onListenProcess,
            )
        executorService.submit(futureTask)
        try {
            onResult.invoke(futureTask.get())
        } catch (e: Exception) {
            onListenProcess.onError(Throwable(e))
        }
    }

    override fun authorize(
        body: AuthorizeRequest,
        onResult: (AuthorizeResponse) -> Unit,
    ) {
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: CustomFuture<AuthorizeResponse> =
            CustomFuture(
                Callable {
                    try {
                        return@Callable Gson().fromJson(
                            postMethodHttp(
                                AUTHORIZE_ENDPOINT,
                                body.toJson(),
                            ),
                            AuthorizeResponse::class.java,
                        )
                    } catch (e: Exception) {
                        Log.e(">>>>>>>>>>", e.message.toString())
                        throw Throwable(e)
                    }
                },
                onListenProcess,
            )
        executorService.submit(futureTask)
        try {
            val response = futureTask.get()
            mToken = response.accessToken
            onResult.invoke(response)
        } catch (e: Exception) {
            onListenProcess.onError(Throwable(e))
        }
    }

    override fun likeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        if (mToken.isNullOrBlank()) {
            onFailure()
        }
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: FutureTask<Unit> =
            FutureTask(
                Callable {
                    try {
                        mToken?.let {
                            postMethodHttpWithBearToken(
                                "photos/$id/like",
                                "",
                                it,
                                onFailure,
                            )
                        }
                        return@Callable
                    } catch (e: Exception) {
                        Log.e(">>>>>>>>>>", e.message.toString())
                        throw Throwable(e)
                    }
                },
            )
        try {
            executorService.submit(futureTask)
        } catch (e: Exception) {
            Log.e(">>>>>>>>>>", e.message.toString())
        }
    }

    override fun dislikeImage(
        id: String,
        onFailure: () -> Unit,
    ) {
        if (mToken.isNullOrBlank()) {
            onFailure()
        }
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: FutureTask<Unit> =
            FutureTask(
                Callable {
                    try {
                        mToken?.let {
                            deleteMethodHttpWithBearToken(
                                "photos/$id/like",
                                it,
                                onFailure,
                            )
                        }
                        return@Callable
                    } catch (e: Exception) {
                        Log.e(">>>>>>>>>>", e.message.toString())
                        throw Throwable(e)
                    }
                },
            )
        try {
            executorService.submit(futureTask)
        } catch (e: Exception) {
            Log.e(">>>>>>>>>>", e.message.toString())
        }
    }

    override fun getFavoriteList(
        name: String,
        page: Int,
        onResult: (List<PhotoModel>) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (mToken.isNullOrBlank()) {
            onFailure()
        }
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: CustomFuture<List<PhotoModel>> =
            CustomFuture(
                Callable {
                    try {
                        val queryValue =
                            mapOf(
                                Pair(PAGE, page.toString()),
                            )
                        return@Callable Gson().fromJsonToList<PhotoModel>(
                            getMethodHttpWithBearToken(
                                "users/$name/likes",
                                queryValue,
                                mToken.toString(),
                                onFailure,
                            ),
                        )
                    } catch (e: Exception) {
                        Log.e(">>>>>>>>", e.message.toString())
                        throw Throwable(e)
                    }
                },
                onListenProcess,
            )
        executorService.submit(futureTask)
        try {
            onResult.invoke(futureTask.get())
        } catch (e: Exception) {
            onListenProcess.onError(Throwable(e))
            Log.e(">>>>>>>>", e.message.toString())
        }
    }

    companion object {
        private var instance: ApiImpl? = null

        @JvmStatic
        fun getInstance(onListenProcess: OnListenProcess): ApiImpl =
            instance ?: ApiImpl(onListenProcess).also { instance = it }
    }
}
