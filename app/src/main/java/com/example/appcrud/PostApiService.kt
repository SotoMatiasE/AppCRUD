package com.example.appcrud

import retrofit2.http.GET

interface PostApiService {
    @GET("posts")

    suspend fun getUserPost() : ArrayList<PostModel>

}