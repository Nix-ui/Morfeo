package com.ucb.morfeo.features.time.presentation

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.time.domain.usecase.GetRealTimeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface TimeUiState {
    object Loading : TimeUiState
    data class Success(val timeMillis: Long) : TimeUiState
    data class Error(val message: String) : TimeUiState
}

class TimeViewModel(
    private val getRealTimeUseCase: GetRealTimeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeUiState>(TimeUiState.Loading)
    val uiState: StateFlow<TimeUiState> = _uiState

    init {
        initializeClock()
    }

    fun retry() {
        initializeClock()
    }

    private fun initializeClock() {
        viewModelScope.launch {
            _uiState.value = TimeUiState.Loading
            try {
                // El repositorio se encarga de la lógica sucia (API vs Local vs Reinicio)
                val offset = getRealTimeUseCase()
                startTicking(offset)
            } catch (e: Exception) {
                _uiState.value = TimeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun startTicking(offset: Long) {
        viewModelScope.launch {
            while (true) {
                // Hora Actual = Tiempo que lleva prendido + Diferencia calculada
                val currentRealTime = SystemClock.elapsedRealtime() + offset
                _uiState.value = TimeUiState.Success(currentRealTime)
                delay(1000)
            }
        }
    }
}