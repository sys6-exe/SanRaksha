package com.example.sanraksha.front

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vitals",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patient_id")]


)
data class Vitals(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    @ColumnInfo(name = "patient_id")
    val patientId : Long,
    @ColumnInfo(name = "date") val date: String,

    @ColumnInfo(name = "age") val Age: Int = 0,

    @ColumnInfo(name = "systolic_bp") val Systolic_BP: Float = 0f,

    @ColumnInfo(name = "diastolic") val Diastolic: Float = 0f,

    @ColumnInfo(name = "bs") val BS: Float = 0f,

    @ColumnInfo(name = "body_temp") val Body_Temp: Float = 0f,

    @ColumnInfo(name = "bmi") val BMI: Float = 0f,

    @ColumnInfo(name = "previous_complications") val Previous_Complications: Int = 0,

    @ColumnInfo(name = "preexisting_diabetes") val Preexisting_Diabetes: Int = 0,

    @ColumnInfo(name = "gestational_diabetes") val Gestational_Diabetes: Int = 0,

    @ColumnInfo(name = "mental_health") val Mental_Health: Int = 0,

    @ColumnInfo(name = "heart_rate") val Heart_Rate: Float = 0f,

    @ColumnInfo(name = "predicted_risk") val predictedRisk: Int = 0


)
