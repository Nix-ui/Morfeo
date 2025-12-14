package com.ucb.morfeo.features.week.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
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

enum class ViewMode {
    WEEK, MONTH
}

class WeeklyDetailsViewModel(
    private val getWeeklySummary: GetWeeklySummaryUseCase,
    private val jwtDataStore: JWTDataStore
) : ViewModel() {

    private val _weeklyState = MutableStateFlow<WeeklySummaryState>(WeeklySummaryState.Loading)
    val weeklyState: StateFlow<WeeklySummaryState> = _weeklyState.asStateFlow()

    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.WEEK)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun loadWeeklySummary(weekStart: LocalDate? = null) {
        viewModelScope.launch {
            _weeklyState.value = WeeklySummaryState.Loading
            try {
                jwtDataStore.getUserMail().fold(
                    onSuccess = { userEmail ->
                        viewModelScope.launch {
                            val startDate = weekStart ?: _selectedDate.value
                            val result = getWeeklySummary(userEmail, startDate)
                            if (result.isSuccess) {
                                _weeklyState.value = WeeklySummaryState.Success(result.getOrThrow())
                                _selectedDate.value = startDate
                            } else {
                                _weeklyState.value = WeeklySummaryState.Error(
                                    result.exceptionOrNull()?.message ?: "Error desconocido"
                                )
                            }
                        }
                    },
                    onFailure = {
                        _weeklyState.value = WeeklySummaryState.Error("No se pudo obtener el email del usuario.")
                    }
                )
            } catch (e: Exception) {
                _weeklyState.value = WeeklySummaryState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }

    fun navigateToPrevious() {
        val newDate = when (_viewMode.value) {
            ViewMode.WEEK -> _selectedDate.value.minus(1, DateTimeUnit.WEEK)
            ViewMode.MONTH -> _selectedDate.value.minus(1, DateTimeUnit.MONTH)
        }
        _selectedDate.value = newDate
        if (_viewMode.value == ViewMode.WEEK) {
            loadWeeklySummary(newDate)
        }
    }

    fun navigateToNext() {
        val newDate = when (_viewMode.value) {
            ViewMode.WEEK -> _selectedDate.value.plus(1, DateTimeUnit.WEEK)
            ViewMode.MONTH -> _selectedDate.value.plus(1, DateTimeUnit.MONTH)
        }
        _selectedDate.value = newDate
        if (_viewMode.value == ViewMode.WEEK) {
            loadWeeklySummary(newDate)
        }
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

private val DateTimeUnit.WEEK: DateTimeUnit.DateBased get() = DateTimeUnit.DayBased(7)
private val DateTimeUnit.MONTH: DateTimeUnit.DateBased get() = DateTimeUnit.MonthBased(1)
