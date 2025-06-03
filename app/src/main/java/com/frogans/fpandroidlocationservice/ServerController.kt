package com.frogans.fpandroidlocationservice

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

enum class RequestType{
    PUT,
    DELETE
}

object ServerController {
    private val client = OkHttpClient()

    fun sendPutRequest(
        json: String
    ){
        sendRequest(json, RequestType.PUT)
    }

    fun sendDeleteRequest(
        json: String
    ){
        sendRequest(json, RequestType.DELETE)
    }

    private fun sendRequest(
        json: String, requestType: RequestType
    ){
        Log.e("ServerController", "JSON: $json ")

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        var request: Request? = null

        when(requestType){
            RequestType.PUT -> {
                request = Request.Builder()
                    .url(BuildConfig.SERVER_URL)
                    .put(requestBody)
                    .build()
                Log.e("ServerController", "Created request of type: ${requestType.toString()}")

            }
            RequestType.DELETE -> {
                request = Request.Builder()
                    .url(BuildConfig.SERVER_URL)
                    .delete(requestBody)
                    .build()
                Log.e("ServerController", "Created request of type: ${requestType.toString()}")
            }
            else -> {
                Log.e("ServerController", "Failed to create request: Request type is invalid")
                return
            }
        }

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ServerController", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("ServerController", "Request succeeded ")
                } else {
                    Log.e("ServerController", "Server error: ${response.code}")
                }
                response.close()
            }
        })
    }
}