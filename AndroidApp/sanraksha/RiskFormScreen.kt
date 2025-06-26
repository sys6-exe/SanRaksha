package com.example.sanraksha

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RiskFormScreen( onSubmit : (riskInput) -> Unit){


    Column( modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var age by remember { mutableStateOf("") }
        var systolicBp by remember { mutableStateOf("") }
        var diastolicBp by remember { mutableStateOf("") }
        var sugarLevel by remember {mutableStateOf("")}
        var bodyTemp by remember {mutableStateOf("")}
        var bmi by remember { mutableStateOf("") }
        var previousComplications by remember { mutableStateOf("") }
        var preexistingDiabetes by remember { mutableStateOf("") }
        var gestationalDiabetes by remember {mutableStateOf("")}
        var mentalHealth by remember{mutableStateOf("")}
        var heartRate by remember{mutableStateOf("")}


        Text("Enter Health details")
        HorizontalDivider()
        Spacer(modifier = Modifier.height(6.dp) )
        OutlinedTextField(value= age, onValueChange = {age = it}, label = {Text("Age")})
        OutlinedTextField(value = systolicBp, onValueChange = {systolicBp = it}, label = {Text("Systolic BP")})
        OutlinedTextField(value = diastolicBp, onValueChange = {diastolicBp = it},label = {Text("Diastolic BP")})
        OutlinedTextField(value = sugarLevel,onValueChange = {sugarLevel = it},label ={Text("Sugar Level")})
        OutlinedTextField(value= bodyTemp, onValueChange = {bodyTemp = it}, label = {Text("Body Temp.")})
        OutlinedTextField(value = bmi, onValueChange = {bmi = it}, label = {Text("BMI")})
        OutlinedTextField(value = previousComplications, onValueChange = {previousComplications = it},label = {Text("Previous Complications")})
        OutlinedTextField(value = preexistingDiabetes,onValueChange = {preexistingDiabetes = it},label ={Text("Preexisting Diabetes")})
        OutlinedTextField(value = gestationalDiabetes,onValueChange = {gestationalDiabetes = it},label ={Text("Gestational Diabetes")})
        OutlinedTextField(value = mentalHealth,onValueChange = {mentalHealth = it},label ={Text("Mental Health")})
        OutlinedTextField(value = heartRate,onValueChange = {heartRate = it},label ={Text("Heart Rate")})

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val input = riskInput(
                Age = age.toIntOrNull()?:0 ,
                Systolic_BP = systolicBp.toIntOrNull()?:0,
                Diastolic = diastolicBp.toIntOrNull()?:0,
                BS = sugarLevel.toFloatOrNull()?:0f,
                Body_Temp = bodyTemp.toFloatOrNull()?:0f,
                BMI = bmi.toFloatOrNull()?:0f,
                Previous_Complications = previousComplications.toIntOrNull()?:0,
                Preexisting_Diabetes = preexistingDiabetes.toIntOrNull()?:0,
                Gestational_Diabetes = gestationalDiabetes.toIntOrNull()?:0,
                Mental_Health = mentalHealth.toIntOrNull()?:0,
                Heart_Rate = heartRate.toIntOrNull()?: 0
            )
                onSubmit(input)


        }) {
            Text("Submit")
        }

    }

}