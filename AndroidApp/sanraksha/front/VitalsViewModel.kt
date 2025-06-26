package com.example.sanraksha.front

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VitalsViewModel(
    private val vitalsRepository: VitalsRepository = Graph.vitalsRepository
): ViewModel() {

    fun insertVitals(vitals : Vitals){
        viewModelScope.launch(Dispatchers.IO) {
            vitalsRepository.insertVitals(vitals)
        }
    }

    fun getVitalsByPatientId(patientId:Long): Flow<List<Vitals>>{
        return vitalsRepository.getVitalsForPatient(patientId)
    }

    fun deleteVitals(vitals: Vitals) {
        viewModelScope.launch(Dispatchers.IO) {
            vitalsRepository.deleteVitals(vitals)
        }
    }

}