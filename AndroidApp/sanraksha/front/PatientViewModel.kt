package com.example.sanraksha.front

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PatientViewModel(
    private val patientRepository: PatientRepository = Graph.patientRepository
): ViewModel() {

 lateinit var getAllPatients : Flow<List<Patient>>

 init{
     viewModelScope.launch {
         getAllPatients = patientRepository.getPatients()
     }
 }
    fun addPatient(patient: Patient){
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.addAPatient(patient)
        }
    }
    fun updatePatient(patient: Patient){
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.updatePatient(patient)
        }
    }
    fun deletePatient(patient: Patient){
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.deletePatient(patient)
        }
    }
    fun getAPatientById(id:Long):Flow<Patient>{
        return patientRepository.getPatientById(id)
    }



}