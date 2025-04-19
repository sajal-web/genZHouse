package com.application.genzhouse.data.remote.api

import com.application.genzhouse.data.remote.model.UserRequest
import com.application.genzhouse.data.remote.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/users/create-user")
    suspend fun createUser(@Body userRequest: UserRequest): Response<UserResponse>
}