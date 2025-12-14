package com.ucb.morfeo.features.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed class DailyDetailsState {
    object Loading : DailyDetailsState()
    data class Success(val sleep: SleepCore) : DailyDetailsState()
    data class Empty(val message: String = "No hay registro para este día.") : DailyDetailsState()
    data class Error(val message: String) : DailyDetailsState()
}

class DailyDetailsViewModel(
    private val jwtDataStore: JWTDataStore,
    private val sleepDao: SleepDao
) : ViewModel() {

    private val _state = MutableStateFlow<DailyDetailsState>(DailyDetailsState.Loading)
    val state: StateFlow<DailyDetailsState> = _state.asStateFlow()

    fun load(date: LocalDate) {
        viewModelScope.launch {
            _state.value = DailyDetailsState.Loading

            jwtDataStore.getUserMail().fold(
                onSuccess = { email ->
                    val sleep = sleepDao.getSleepByDate(email, date)
                    _state.value = if (sleep != null) {
                        DailyDetailsState.Success(sleep)
                    } else {
                        DailyDetailsState.Empty()
                    }
                },
                onFailure = {
                    _state.value = DailyDetailsState.Error("No se pudo obtener el email del usuario.")
                }
            )
        }
    }
}
