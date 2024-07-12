package com.example.imageEditor.ui.favourite.adapter

interface OnClickImage {
    fun likeImage(id: String)

    fun dislikeImage(id: String)

    fun clickDetailImage(url: String)
}
