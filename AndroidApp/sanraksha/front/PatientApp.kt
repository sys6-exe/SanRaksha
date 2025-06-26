package com.example.sanraksha.front

import android.app.Application

class PatientApp : Application() {
    override fun onCreate(){
        super.onCreate()
        Graph.provide(this)
    }
}