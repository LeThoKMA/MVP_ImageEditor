package com.example.imageEditor.ui.home.adapter

interface OnClickImage {
    fun clickImage(url: String)

    fun doubleTapForLikeImage(id: String)

    fun clickLike(index: Int)
}
