package com.example.sanraksha

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected : Flow<Boolean>
}