package com.example.sanraksha

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object retrofitInstance {
    private const val BASE_URL = "https://sanraksha.onrender.com/"




    val api : healthApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(healthApiService::class.java)
    }
}