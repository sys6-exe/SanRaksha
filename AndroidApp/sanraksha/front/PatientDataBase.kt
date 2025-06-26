package com.example.sanraksha.front

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Patient::class,Vitals ::class],
    version = 7,
    exportSchema = false
)
abstract class PatientDataBase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun vitalsDao(): VitalsDao
}