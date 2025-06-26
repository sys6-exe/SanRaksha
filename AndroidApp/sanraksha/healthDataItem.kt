package com.example.sanraksha



data class healthDataItem(
          val Age: Int,
          val Systolic_BP: Float? = null,
          val Diastolic: Float? = null,
          val BS: Float? = null,
          val Body_Temp: Float? = null,
          val BMI: Float,
          val Previous_Complications: Int? = null,
          val Preexisting_Diabetes: Int? = null,
          val Gestational_Diabetes: Int? = null,
          val Mental_Health: Int? = null,
          val Heart_Rate: Float? = null
)

