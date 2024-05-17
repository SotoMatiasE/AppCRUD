package com.example.appcrud

import com.google.gson.annotations.SerializedName

data class PostModelResponse (
    @SerializedName("userId")
    var userId: Int,
    @SerializedName("id")
    var id: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("body")
    val body: String
)