package com.example.sanraksha.front

 sealed class Screens(val route:String) {
     object Home : Screens("home")
     object AddPatient : Screens("add_patient")
     object Profile : Screens("profile/{patientId}"){
         fun passId(patientId:Long):String = "profile/$patientId"
     }
     object RecordVitals:Screens("recordvitals/{patientId}"){
         fun passId(patientId: Long):String = "recordvitals/$patientId"
     }


}