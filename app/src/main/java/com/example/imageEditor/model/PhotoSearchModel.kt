package com.example.imageEditor.model

import com.google.gson.annotations.SerializedName

data class PhotoSearchModel(
    @SerializedName("results")
    val photoModels: List<PhotoModel>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
)
