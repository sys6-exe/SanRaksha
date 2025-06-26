package com.example.sanraksha

data class riskInput(
 val Age : Int = 0 ,
 val Systolic_BP : Int = 0,
 val Diastolic : Int = 0 ,
 val BS : Float = 0f,
 val Body_Temp : Float = 0f , // In degree celcius
 val BMI : Float = 0f ,
 val Previous_Complications : Int = 0 , // will be zero or 1
 val Preexisting_Diabetes : Int = 0 , // will be zero or 1
 val Gestational_Diabetes : Int = 0, //will be zero or 1
 val Mental_Health : Int = 0 , // will be zero or 1
 val Heart_Rate : Int = 0 ,
)
