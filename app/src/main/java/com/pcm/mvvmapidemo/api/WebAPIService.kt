package com.pcm.mvvmapidemo.api

import com.pcm.mvvmapidemo.data.User
import retrofit2.Call
import retrofit2.http.GET

interface WebAPIService {

    @GET("users")
    fun getUsers(): Call<List<User>>
}