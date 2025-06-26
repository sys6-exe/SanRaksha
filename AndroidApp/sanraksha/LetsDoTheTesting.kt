package com.example.sanraksha

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ApiTestScreen() {
    val scope = rememberCoroutineScope()

    //    "Age": 30,
    //    "Systolic_BP": 120,
    //    "Diastolic": 80,
    //    "BS": 6.3,
    //    "Body_Temp": 99.1,
    //    "BMI": 28,
    //    "Previous_Complications": 1,
    //    "Preexisting_Diabetes": 1,
    //    "Gestational_Diabetes": 0,
    //    "Mental_Health": 1,
    //    "Heart_Rate": 85

    Button(onClick = {
        scope.launch {
            try {
                val healthDataList = listOf(
                    healthDataItem(
                        Age = 30,
                        Systolic_BP = 120f,
                        Diastolic = 80f,
                        BS = 6.3f,
                        Body_Temp = 99.1f,
                        BMI = 28f,
                        Previous_Complications = 1,
                        Preexisting_Diabetes = 1,
                        Gestational_Diabetes = 0,
                        Mental_Health = 1,
                        Heart_Rate = 85f
                    )
                )

                val response = retrofitInstance.api.sendHealthData(healthDataList)

                if (response.isSuccessful) {
                    val prediction = response.body()?.prediction
                    Log.d("API_SUCCESS", "Prediction: $prediction")
                } else {
                    Log.e("API_ERROR", "Response Code: ${response.code()}")
                }
            } catch (e: Exception) {

                Log.e("API_EXCEPTION", "Exception: ${e.message}")
            }
        }
    }) {
        Text("Send Test API Request")
    }
}







