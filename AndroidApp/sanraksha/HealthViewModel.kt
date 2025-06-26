package com.example.sanraksha

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    fun sendHealthDataToApi(inputs : healthDataItem,onResult : (Int?)->Unit){
        val healthDataList = listOf(
            inputs
        )
        viewModelScope.launch {
            try{
                val response = retrofitInstance.api.sendHealthData(healthDataList)
                if (response.isSuccessful) {
                    val prediction = response.body()?.prediction?.firstOrNull()
                    Log.d("API_SUCCESS", "Prediction: $prediction")
                    onResult(prediction)
                } else {
                    Log.e("API_ERROR", "Response Code: ${response.code()}")
                    Log.e("API_ERROR", "Error Body: ${response.errorBody()?.string()}")
                    onResult(null)
                }

            }catch(e : Exception){
                Log.d("API_REQUEST", "Sending data: $healthDataList")
                Log.e("API_EXCEPTION", "Exception: ${e.message}", e)
               onResult(null)
        }
        }

    }

}