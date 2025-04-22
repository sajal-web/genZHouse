package com.application.genzhouse.utils

import com.application.genzhouse.data.remote.model.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.error ?: errorResponse.message
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }

            when (response.code()) {
                400 -> Resource.Error("Bad Request: $errorMessage")
                401 -> Resource.Error("Unauthorized: $errorMessage")
                403 -> Resource.Error("Forbidden: $errorMessage")
                404 -> Resource.Error("Not Found: $errorMessage")
                409 -> Resource.Error("Conflict: $errorMessage")
                422 -> Resource.Error("Validation Error: $errorMessage")
                500 -> Resource.Error("Internal Server Error: $errorMessage")
                502 -> Resource.Error("Bad Gateway: $errorMessage")
                503 -> Resource.Error("Service Unavailable: $errorMessage")
                else -> Resource.Error("HTTP ${response.code()}: $errorMessage")
            }
        }
    } catch (e: IOException) {
        Resource.Error(e.toString())
    } catch (e: Exception) {
        Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error occurred"}")
    }
}
