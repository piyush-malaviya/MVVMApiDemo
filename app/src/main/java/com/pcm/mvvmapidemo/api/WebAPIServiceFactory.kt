package com.pcm.mvvmapidemo.api

import androidx.viewbinding.BuildConfig
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class WebAPIServiceFactory : OkHttpClientHelper.OnInterceptRequestResponse {

    companion object {
        const val BASE_URL = "https://api.github.com/"

        fun newInstance(): WebAPIServiceFactory {
            return WebAPIServiceFactory()
        }
    }

    private fun getHeaderMap(): HashMap<String, String> {
        val headerMap = HashMap<String, String>()
        headerMap["Content-Type"] = "application/json"
        return headerMap
    }

    fun makeServiceFactory(): WebAPIService {
        val okHttpClient = OkHttpClientHelper.newInstance()
            .withLoggingInterceptor(BuildConfig.DEBUG)
            .withRequestInterceptor(this)
            .withResponseInterceptor(this)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(WebAPIService::class.java)
    }

    override fun onInterceptResponse(response: Response) {
    }

    override fun onInterceptRequest(): HashMap<String, String> {
        return getHeaderMap()
    }

}