package com.example.sanraksha.front

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VitalsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertVitals(vitals : Vitals)

    @Query("SELECT * FROM vitals WHERE patient_id = :patientId ORDER BY id ASC")
    abstract fun getVitalsForPatient(patientId : Long): Flow<List<Vitals>>

    @Delete
    abstract suspend fun deleteVitals(vitals: Vitals)
}