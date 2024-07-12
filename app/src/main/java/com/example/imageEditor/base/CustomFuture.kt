package com.example.imageEditor.base

import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

class CustomFuture<T>(
    callable: Callable<T>,
    private val onListenProcess: OnListenProcess,
) : FutureTask<T>(callable) {
    override fun get(): T {
        onListenProcess.onProgress()
        return super.get()
    }

    override fun done() {
        onListenProcess.onSuccess()
        super.done()
    }
}
