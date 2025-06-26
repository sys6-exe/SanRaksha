package com.example.sanraksha.front

import android.content.Context
import android.util.Log
import androidx.room.Room


object Graph {
    lateinit var database : PatientDataBase

    val patientRepository by lazy {
        PatientRepository(patientDao = database.patientDao())
    }

    val vitalsRepository by lazy{
        VitalsRepository(vitalsDao = database.vitalsDao())
    }


    fun provide(context: Context){
        try {
            database = Room.databaseBuilder(context, PatientDataBase::class.java, "patientlist.db")
                .build()
        }catch (e: Exception) {
            Log.e("DB_ERROR", "Room DB init failed", e)
        }
    }

}