package com.example.imageEditor.model.request

import com.google.gson.annotations.SerializedName

data class AuthorizeRequest(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("redirect_uri")
    val redirectUri: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("grant_type")
    val grantType: String = "authorization_code",
)
