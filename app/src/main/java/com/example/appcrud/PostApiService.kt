package com.example.appcrud

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {
    @GET("posts")

    suspend fun getUserPost() : ArrayList<PostModelResponse>


    //parametro dinamico
    @GET("posts/{id}")
    suspend fun getUserPostById(@Path("id") id: String) : Response<PostModelResponse>
}