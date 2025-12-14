package com.ucb.morfeo.features.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.week.domain.usecase.GetWeeklySummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

sealed class DailyDetailsState {
    object Loading : DailyDetailsState()

    data class Success(
        val sleep: SleepCore,
        val weeklyAvgDurationMinutes: Long,
        val weeklyAvgScore: Int
    ) : DailyDetailsState()

    data class Empty(val message: String = "No hay registro para este día.") : DailyDetailsState()
    data class Error(val message: String) : DailyDetailsState()
}

class DailyDetailsViewModel(
    private val jwtDataStore: JWTDataStore,
    private val sleepDao: SleepDao,
    private val getWeeklySummary: GetWeeklySummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DailyDetailsState>(DailyDetailsState.Loading)
    val state: StateFlow<DailyDetailsState> = _state.asStateFlow()

    private fun startOfWeek(date: LocalDate): LocalDate {
        // Monday=0 ... Sunday=6
        val daysSinceMonday = date.dayOfWeek.ordinal
        return date.minus(daysSinceMonday.toLong(), DateTimeUnit.DAY)
    }

    fun load(date: LocalDate) {
        viewModelScope.launch {
            _state.value = DailyDetailsState.Loading

            jwtDataStore.getUserMail().fold(
                onSuccess = success@ { email ->
                    val sleep = sleepDao.getSleepByDate(email, date)
                    if (sleep == null) {
                        _state.value = DailyDetailsState.Empty()
                        return@success
                    }

                    val weekStart = startOfWeek(date)
                    val weeklyResult = getWeeklySummary(email, weekStart)
                    if (weeklyResult.isFailure) {
                        _state.value = DailyDetailsState.Error(
                            weeklyResult.exceptionOrNull()?.message
                                ?: "No se pudo cargar el promedio semanal."
                        )
                        return@success
                    }

                    val weekly = weeklyResult.getOrThrow()
                    _state.value = DailyDetailsState.Success(
                        sleep = sleep,
                        weeklyAvgDurationMinutes = weekly.averageSleepDuration.inWholeMinutes,
                        weeklyAvgScore = weekly.averageSleepScore
                    )
                },
                onFailure = {
                    _state.value = DailyDetailsState.Error("No se pudo obtener el email del usuario.")
                }
            )
        }
    }
}
