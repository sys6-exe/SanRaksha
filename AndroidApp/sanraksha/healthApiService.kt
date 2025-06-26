package com.example.sanraksha

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface healthApiService {
    @POST("predict")
    suspend fun sendHealthData(
        @Body healthData : List<healthDataItem>
    ): Response<predictionResponse>
}