package com.example.sanraksha.front

import kotlinx.coroutines.flow.Flow

class VitalsRepository(
    private val vitalsDao : VitalsDao ) {

    suspend fun insertVitals(vitals : Vitals){
        vitalsDao.insertVitals(vitals)
    }

    fun getVitalsForPatient(patientId:Long): Flow<List<Vitals>> {
        return vitalsDao.getVitalsForPatient(patientId)
    }

    suspend fun deleteVitals(vitals:Vitals){
        vitalsDao.deleteVitals(vitals)
    }


}