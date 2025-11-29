package com.ucb.morfeo.features.time.presentation

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.time.data.local.TimeLocalDataSource
import com.ucb.helpet.features.time.data.remote.TimeApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.Exception

sealed interface TimeUiState {
    object Loading : TimeUiState
    data class Success(val time: Long) : TimeUiState
    data class Error(val message: String) : TimeUiState
}

class TimeViewModel(private val timeApi: TimeApi, private val localDataSource: TimeLocalDataSource) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeUiState>(TimeUiState.Loading)
    val uiState: StateFlow<TimeUiState> = _uiState

    private var timeOffset = 0L

    init {
        viewModelScope.launch {
            try {
                // 1. Intentar obtener la hora de la red
                val response = timeApi.getCurrentTime()
                val serverTime = TimeUnit.SECONDS.toMillis(response.unixtime)
                val elapsedRealtime = SystemClock.elapsedRealtime()

                // 2. Guardar la instantánea de tiempo correcta
                localDataSource.saveTimeSnapshot(serverTime, elapsedRealtime)
                
                // Calcular offset y empezar a contar
                timeOffset = serverTime - elapsedRealtime
                startTicking()
            } catch (e: Exception) {
                // 3. Si la red falla, intentar cargar desde el almacenamiento local
                val snapshot = localDataSource.getTimeSnapshot()
                if (snapshot != null) {
                    val (serverTime, elapsedRealtime) = snapshot
                    timeOffset = serverTime - elapsedRealtime
                    startTicking()
                } else {
                    // 4. Si no hay red ni datos locales, mostrar error
                    _uiState.value = TimeUiState.Error("Se necesita conexión a internet la primera vez.")
                }
            }
        }
    }

    private fun startTicking() {
        viewModelScope.launch {
            while (true) {
                val currentTime = SystemClock.elapsedRealtime() + timeOffset
                _uiState.value = TimeUiState.Success(currentTime)
                delay(1000)
            }
        }
    }
}
