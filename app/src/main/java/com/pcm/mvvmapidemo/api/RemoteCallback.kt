package com.pcm.mvvmapidemo.api

import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

abstract class RemoteCallback<T> : Callback<T> {

    /**
     * Overrides onResponse method and handles response of servers and reacts accordingly.
     *
     * @param call
     * @param response
     */
    override fun onResponse(call: Call<T>, response: Response<T>) {
        onComplete()

        when (response.code()) {
            HttpsURLConnection.HTTP_OK,
            HttpsURLConnection.HTTP_CREATED,
            HttpsURLConnection.HTTP_ACCEPTED,
            HttpsURLConnection.HTTP_NOT_AUTHORITATIVE ->
                if (response.body() != null) {
                    onSuccess(response.body())
                } else {
                    onEmptyResponse()
                }
            HttpURLConnection.HTTP_NO_CONTENT -> onEmptyResponse()
            HttpURLConnection.HTTP_UNAUTHORIZED -> onUnauthorized(Throwable(getErrorMessage(response)))
            HttpURLConnection.HTTP_NOT_FOUND -> onUserNotExist(getErrorMessage(response))
            else -> onFailed(Throwable(getErrorMessage(response)))
        }
    }

    private fun getErrorMessage(response: Response<T>?): String {
        if (response?.errorBody() == null) {
            return DEFAULT_ERROR_MSG
        }

        val jObjError: JSONObject
        try {
            jObjError = JSONObject(response.errorBody()!!.string())
        } catch (e: JSONException) {
            return DEFAULT_ERROR_MSG
        } catch (e: IOException) {
            return DEFAULT_ERROR_MSG
        }

        //gets message value which is returned by server
        var errorMessage = jObjError.optString("message", DEFAULT_ERROR_MSG)
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = DEFAULT_ERROR_MSG
        }
        return errorMessage
    }

    /**
     * Overriding default onFailure method
     * this method will trigger onInternetFailed()
     *
     * @param call
     * @param t
     */
    override fun onFailure(call: Call<T>, t: Throwable) {
        onComplete()

        if (t is ConnectException) {
            //Add your code for displaying no network connection error
            onInternetFailed()
        } else if (!call.isCanceled) {
            onFailed(Throwable(DEFAULT_ERROR_MSG))
        } else {
            onCancel()
        }
    }

    /**
     * onSuccess will be called when response contains body
     *
     * @param response
     */
    abstract fun onSuccess(response: T?)

    /**
     * onUnauthorized will be called when token miss matches with server
     * It is optional, Override it for handle un-authorization
     */
    open fun onUnauthorized(throwable: Throwable) {}

    /**
     * onFailed will be called when error generated from server
     *
     * @param throwable message value will be dependend on servers error message
     * if message is not available from server than default error message will
     * be displayed.
     */
    abstract fun onFailed(throwable: Throwable)

    /**
     * onInternetFailed() method will be called when
     * network connection is not available in device.
     */
    abstract fun onInternetFailed()

    /**
     * onEmptyResponse() method will be called when response from server is blank or
     * error code is 404 generated.
     * It is optional, Override it for handle empty response
     */
    open fun onEmptyResponse() {}

    open fun onCancel() {}

    /**
     * Called when api call completed (Successfully or Failed)
     * Useful for operation like hide progress bar or dialog
     */
    open fun onComplete() {}

    open fun onUserNotExist(message: String) {}

    open fun onProgress(bytesRead: Long, contentLength: Long, done: Boolean) {}

    companion object {

        // Default error message
        private const val DEFAULT_ERROR_MSG = "Unable to reach server."
    }

}