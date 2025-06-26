package com.example.sanraksha.front

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PatientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addAPatient(patientEntity:Patient)

    @Query("Select * from `patient-table`")
    abstract fun getAllPatient(): Flow<List<Patient>>

    @Update
    abstract suspend fun updatePatient(patientEntity: Patient)

    @Delete
    abstract suspend fun deletePatient(PatientEntity:Patient)

    @Query("Select * from `patient-table` where id=:id")
    abstract fun getPatientById(id:Long):Flow<Patient>

}