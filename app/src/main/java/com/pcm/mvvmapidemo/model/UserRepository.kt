package com.pcm.mvvmapidemo.model

import com.pcm.mvvmapidemo.api.WebAPIService
import com.pcm.mvvmapidemo.data.User
import retrofit2.Call

class UserRepository(private val apiServices: WebAPIService) {

    fun getUsers(): Call<List<User>> {
        return apiServices.getUsers()
    }


}