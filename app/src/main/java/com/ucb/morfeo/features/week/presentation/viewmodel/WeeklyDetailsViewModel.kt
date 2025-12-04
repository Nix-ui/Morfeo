// features/week/presentation/viewmodel/WeeklyDetailsViewModel.kt
package com.ucb.morfeo.features.week.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import com.ucb.morfeo.features.week.domain.usecase.GetWeeklySummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class WeeklyDetailsViewModel(
    private val getWeeklySummary: GetWeeklySummaryUseCase
) : ViewModel() {

    private val _weeklyState = MutableStateFlow<WeeklySummaryState>(WeeklySummaryState.Loading)
    val weeklyState: StateFlow<WeeklySummaryState> = _weeklyState.asStateFlow()

    private val _selectedWeek: MutableStateFlow<LocalDate> = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    val selectedWeek: StateFlow<LocalDate> = _selectedWeek.asStateFlow()

    fun loadWeeklySummary(weekStart: LocalDate? = null) {
        viewModelScope.launch {
            _weeklyState.value = WeeklySummaryState.Loading
            try {
                val result = getWeeklySummary(weekStart ?: _selectedWeek.value)
                if (result.isSuccess) {
                    _weeklyState.value = WeeklySummaryState.Success(result.getOrThrow())
                    weekStart?.let { _selectedWeek.value = it }
                } else {
                    _weeklyState.value = WeeklySummaryState.Error(
                        result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            } catch (e: Exception) {
                _weeklyState.value = WeeklySummaryState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }

    fun navigateToPreviousWeek() {
        val newWeek = _selectedWeek.value.minus(7, DateTimeUnit.DAY)
        _selectedWeek.value = newWeek
        loadWeeklySummary(newWeek)
    }

    fun navigateToNextWeek() {
        val newWeek = _selectedWeek.value.plus(7, DateTimeUnit.DAY)
        _selectedWeek.value = newWeek
        loadWeeklySummary(newWeek)
    }

    init {
        loadWeeklySummary()
    }
}

sealed class WeeklySummaryState {
    object Loading : WeeklySummaryState()
    data class Success(val weeklySummary: WeeklySummary) : WeeklySummaryState()
    data class Error(val message: String) : WeeklySummaryState()
}