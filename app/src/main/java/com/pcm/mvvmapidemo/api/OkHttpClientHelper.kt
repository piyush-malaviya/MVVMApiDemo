package com.pcm.mvvmapidemo.api

import okhttp3.Headers.Companion.toHeaders
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit

class OkHttpClientHelper {

    private val httpClientBuilder = OkHttpClient().newBuilder()

    companion object {
        private const val HTTP_READ_TIMEOUT = 10000L
        private const val HTTP_CONNECT_TIMEOUT = 6000L

        @JvmStatic
        fun newInstance(): OkHttpClientHelper {
            return OkHttpClientHelper()
        }
    }

    init {
        httpClientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
    }

    fun withInterceptor(interceptor: Interceptor): OkHttpClientHelper {
        httpClientBuilder.interceptors().add(interceptor)
        return this
    }

    fun withInterceptors(interceptors: ArrayList<Interceptor>?): OkHttpClientHelper {
        if (interceptors != null) {
            httpClientBuilder.interceptors().addAll(interceptors)
        }
        return this
    }

    fun withLoggingInterceptor(debug: Boolean): OkHttpClientHelper {
        httpClientBuilder.interceptors().add(HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        })
        return this
    }

    fun withRequestInterceptor(onInterceptRequest: OnInterceptRequestResponse?): OkHttpClientHelper {
        httpClientBuilder.interceptors().add(Interceptor { chain ->
            val original = chain.request()

            val headerMap = HashMap<String, String>()
            if (onInterceptRequest != null) {
                headerMap.putAll(onInterceptRequest.onInterceptRequest())
            }

            // Customize the request
            val request = original.newBuilder()
                .headers(headerMap.toHeaders())
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        })
        return this
    }

    fun withResponseInterceptor(onInterceptResponse: OnInterceptRequestResponse?): OkHttpClientHelper {
        httpClientBuilder.interceptors().add(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            var strResponse = ""
            if (response.body != null) {
                strResponse = response.body!!.string()
            }

            onInterceptResponse?.onInterceptResponse(response)

            response.newBuilder()
                .body(strResponse.toResponseBody(response.body!!.contentType()))
                .build()
        })
        return this
    }

    fun build(): OkHttpClient {
        return httpClientBuilder.build()
    }

    interface OnInterceptRequestResponse {
        fun onInterceptResponse(response: Response)
        fun onInterceptRequest(): HashMap<String, String>
    }

}