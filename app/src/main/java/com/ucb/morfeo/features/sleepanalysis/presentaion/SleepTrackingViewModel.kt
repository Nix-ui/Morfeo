package com.ucb.morfeo.features.sleepanalysis.presentaion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepSession
import com.ucb.morfeo.features.sleepanalysis.domain.usecase.AnalizeSleepAudioUseCase
import com.ucb.morfeo.features.sleepanalysis.domain.usecase.CancelSleepSessionUseCase
import com.ucb.morfeo.features.sleepanalysis.domain.usecase.GetSleepHistoryUseCase
import com.ucb.morfeo.features.sleepanalysis.domain.usecase.StartSleepTrackingUseCase
import com.ucb.morfeo.features.sleepanalysis.domain.usecase.StopSleepTrackingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SleepTrackingViewModel(
    private val startSleepTrackingUseCase: StartSleepTrackingUseCase,
    private val stopSleepTrackingUseCase: StopSleepTrackingUseCase,
    private val analyzeSleepTrackingUseCase: AnalizeSleepAudioUseCase,
    private val getSleepHistoryUseCase: GetSleepHistoryUseCase,
    private val cancelSleepSessionUseCase: CancelSleepSessionUseCase
): ViewModel() {
    data class SleepTrakingUiState(
        val currentSession: SleepSession? = null,
        val lastAnalysis: SleepAnalysis? =null,
        val sleepHistory: List<SleepAnalysis> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
    sealed class RecordingState{
        object Idle: RecordingState()
        object Starting : RecordingState()
        data class Recording(val session: SleepSession): RecordingState()
        object Stopping : RecordingState()
        object  Cancelling: RecordingState()
        data class Completed(val analysis: SleepAnalysis): RecordingState()
        data class  Error(val message:String): RecordingState()
    }
    private val _uiState = MutableStateFlow(SleepTrakingUiState())
    val uiState: StateFlow<SleepTrakingUiState> = _uiState.asStateFlow()

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    fun startSleepTracking(userEmail:String, scheduledWakeTime: LocalDateTime){
        viewModelScope.launch {
            _recordingState.value = RecordingState.Starting
            updateUiState{ copy(isLoading = true)}
            val result = startSleepTrackingUseCase(
                userEmail = userEmail,
                bedTime = getCurrentDateTime(),
                scheduleWakeTime = scheduledWakeTime
            )
            result.onSuccess { session ->
                _recordingState.value = RecordingState.Recording(session)
                updateUiState{ copy(isLoading = false,currentSession=session)}
            }.onFailure {error ->
                _recordingState.value = RecordingState.Error(error.message ?: "Error Desconocido")
                updateUiState{ copy(isLoading = false,error = error.message)}
            }
        }
    }
    fun stopAnalyzeSleep(){
        viewModelScope.launch {
            val currentSession = _uiState.value.currentSession ?: return@launch
            _recordingState.value = RecordingState.Stopping
            updateUiState{ copy(isLoading = true)}
            val result = stopSleepTrackingUseCase(
                sessionId = currentSession.id,
                wakeTime = getCurrentDateTime()
            )
            result.onSuccess { analysis ->
                _recordingState.value = RecordingState.Completed(analysis)
                updateUiState{ copy(
                    lastAnalysis = analysis,
                    currentSession= null,
                    isLoading = false
                )}
            }.onFailure {error ->
                _recordingState.value = RecordingState.Error(error.message ?: "Analisi fallido")
                updateUiState{ copy(isLoading = false,error = error.message)}
            }
        }
    }
    fun cancelCurrentRecording(){
        viewModelScope.launch {
            val currentSession = _uiState.value.currentSession ?: return@launch
            _recordingState.value = RecordingState.Cancelling
            updateUiState{ copy(isLoading = true)}
            val result = cancelSleepSessionUseCase(currentSession.id)
            result.onSuccess {
                _recordingState.value = RecordingState.Idle
                updateUiState{ copy(isLoading = false,currentSession= null)}
            }.onFailure { error->
                _recordingState.value = RecordingState.Error(error.message?:"Fallo al Cancelar")
                updateUiState{ copy(isLoading = false,error = error.message)}
            }
        }
    }
    fun clearError(){
        updateUiState{ copy(error = null)}
    }
    fun loadSleepHistory(userEmail: String){
        viewModelScope.launch {
            updateUiState{ copy(isLoading = true)}
            try{
                getSleepHistoryUseCase(userEmail).collect { history->
                    updateUiState{ copy(isLoading = false,sleepHistory= history)}
                }
            }catch (e: Exception){
                updateUiState{ copy(isLoading = false,error = "Failed to load history: ${e.message}")}
            }
        }
    }
    private inline fun updateUiState(transform: SleepTrakingUiState.() -> SleepTrakingUiState){
        _uiState.update { it.transform() }
    }
    private fun getCurrentDateTime(): LocalDateTime{
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}