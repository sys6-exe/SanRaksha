package com.example.sanraksha.front

import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val patientDao : PatientDao ) {

    suspend fun addAPatient(patient : Patient){
        patientDao.addAPatient(patient)
    }

    fun getPatients(): Flow<List<Patient>> = patientDao.getAllPatient()

    fun getPatientById(id:Long) :Flow<Patient>{
        return patientDao.getPatientById(id)
    }

    suspend fun updatePatient(patient: Patient){
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatient(patient: Patient){
        patientDao.deletePatient(patient)
    }


}