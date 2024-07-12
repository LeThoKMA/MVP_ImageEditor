package com.example.imageEditor.apiService.local

import android.content.Context
import android.util.Log
import com.example.imageEditor.base.OnListenProcess
import com.example.imageEditor.model.QueryModel
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class DataSourceImpl(private val dbHelper: DBHelper, private val onListenProcess: OnListenProcess) :
    DataSource {
    override fun saveQuery(
        query: String,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        dbHelper.addData(query)
        getAllQuery(onResult)
    }

    override fun deleteQuery(
        id: Long,
        onResult: (List<QueryModel>) -> Unit,
    ) {
        dbHelper.deleteData(id)
        getAllQuery(onResult)
    }

    override fun getAllQuery(onResult: (List<QueryModel>) -> Unit) {
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        val futureTask: FutureTask<List<QueryModel>> =
            FutureTask(
                Callable {
                    try {
                        return@Callable dbHelper.getAllData()
                    } catch (e: Exception) {
                        throw Throwable(e)
                    }
                },
            )
        executorService.submit(futureTask)
        try {
            onResult.invoke(futureTask.get())
        } catch (e: Exception) {
            Log.e(">>>>>>", e.toString())
            onListenProcess.onError(Throwable(e))
        } finally {
            executorService.shutdown()
        }
    }

    companion object {
        private var instance: DataSourceImpl? = null

        @JvmStatic
        fun getInstance(
            context: Context,
            onListenProcess: OnListenProcess,
        ): DataSourceImpl =
            instance ?: DataSourceImpl(
                DBHelper.getInstance(context),
                onListenProcess,
            ).also { instance = it }
    }
}
