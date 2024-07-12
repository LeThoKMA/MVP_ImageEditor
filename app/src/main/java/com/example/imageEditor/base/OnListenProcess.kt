package com.example.imageEditor.base

interface OnListenProcess {
    fun onProgress()

    fun onSuccess()

    fun onError(throwable: Throwable)
}
