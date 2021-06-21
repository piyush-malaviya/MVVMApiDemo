package com.pcm.mvvmapidemo.viewmodel

import androidx.lifecycle.*
import com.pcm.mvvmapidemo.api.RemoteCallback
import com.pcm.mvvmapidemo.data.User
import com.pcm.mvvmapidemo.model.UserRepository


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val userList = MutableLiveData<List<User>?>()
    val errorMessage = MutableLiveData<String>()
    val loadingStatus = MutableLiveData<Boolean>()


    fun getUsers() {
        loadingStatus.postValue(true)
        repository.getUsers().enqueue(object : RemoteCallback<List<User>>() {
            override fun onSuccess(response: List<User>?) {
                userList.postValue(response)
            }

            override fun onFailed(throwable: Throwable) {
                errorMessage.postValue(throwable.message)
            }

            override fun onInternetFailed() {
                errorMessage.postValue("Internet Connection Failed")
            }

            override fun onComplete() {
                loadingStatus.postValue(false)
            }
        })
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}