package com.example.sanraksha

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


class ConnectivityVIewModel(
    private val connectivityObserver: ConnectivityObserver
):ViewModel() {

    val isConnected = connectivityObserver.isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )
}