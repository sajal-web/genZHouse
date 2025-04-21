package com.application.genzhouse.data.remote.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://2574-2405-201-8044-283a-a836-8b48-5c10-b8d7.ngrok-free.app/"

    private val gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting() // Makes JSON more readable when printed
        .create()

    // Custom logger to format JSON responses
    private val logger = HttpLoggingInterceptor.Logger { message ->
        when {
            message.startsWith("{") || message.startsWith("[") -> {
                // Pretty print JSON
                try {
                    val prettyJson = gson.toJson(gson.fromJson(message, Any::class.java))
                    println("\n╔═══════════════════════════════════════════════════")
                    println("║ JSON Response")
                    println("╠═══════════════════════════════════════════════════")
                    println(prettyJson)
                    println("╚═══════════════════════════════════════════════════\n")
                } catch (e: Exception) {
                    println("║ $message")
                }
            }
            else -> {
                println("║ $message")
            }
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val roomApiService: RoomApiService = retrofit.create(RoomApiService::class.java)
    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
}