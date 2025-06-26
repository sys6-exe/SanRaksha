package com.example.sanraksha.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sanraksha.ConnectivityObserver
import com.example.sanraksha.ConnectivityVIewModel

class ConnectivityViewModelFactory(
    private val observer : ConnectivityObserver
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectivityVIewModel::class.java)) {
            return ConnectivityVIewModel(observer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}