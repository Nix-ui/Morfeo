package com.ucb.morfeo.features.week.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

enum class ViewMode { WEEK, MONTH }

class WeeklyDetailsViewModel(
    private val getWeeklySummary: GetWeeklySummaryUseCase,
    private val jwtDataStore: JWTDataStore,
    private val sleepDao: SleepDao
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
        // (mensual real lo vemos después)
    }

    private fun startOfWeek(date: LocalDate): LocalDate {
        // ordinal: Monday=0, Tuesday=1, ... Sunday=6
        val daysSinceMonday = date.dayOfWeek.ordinal
        return date.minus(daysSinceMonday.toLong(), DateTimeUnit.DAY)
    }

    fun loadWeeklySummary(weekStart: LocalDate? = null) {
        viewModelScope.launch {
            _weeklyState.value = WeeklySummaryState.Loading

            jwtDataStore.getUserMail().fold(
                onSuccess = { userEmail ->
                    val base = weekStart ?: _selectedDate.value
                    val startDate = startOfWeek(base)

                    val result = getWeeklySummary(userEmail, startDate)
                    if (result.isSuccess) {
                        _weeklyState.value = WeeklySummaryState.Success(result.getOrThrow())
                        _selectedDate.value = startDate
                    } else {
                        _weeklyState.value = WeeklySummaryState.Error(
                            result.exceptionOrNull()?.message ?: "Error desconocido"
                        )
                    }
                },
                onFailure = {
                    _weeklyState.value = WeeklySummaryState.Error("No se pudo obtener el email del usuario.")
                }
            )
        }
    }

    /**
     * Inserta datos demo SOLO si esta semana no tiene registros en sleep_core.
     * Luego recarga el resumen.
     */
    fun seedDemoWeek() {
        viewModelScope.launch {
            jwtDataStore.getUserMail().onSuccess { userEmail ->
                val startDate = startOfWeek(_selectedDate.value)
                val endDate = startDate.plus(6, DateTimeUnit.DAY)

                val existing = sleepDao.getSleepSessionsBetween(userEmail, startDate, endDate)
                if (existing.isNotEmpty()) {
                    loadWeeklySummary(startDate)
                    return@onSuccess
                }

                val demo = (0..6).map { i ->
                    val d = startDate.plus(i, DateTimeUnit.DAY)

                    val minutes = listOf(420L, 390L, 450L, 360L, 480L, 510L, 400L)[i] // 6h–8.5h
                    val score = listOf(72, 65, 80, 58, 85, 90, 70)[i]

                    val bed = LocalDateTime(d.year, d.monthNumber, d.dayOfMonth, 23, 0)
                    val next = d.plus(1, DateTimeUnit.DAY)
                    val wake = LocalDateTime(next.year, next.monthNumber, next.dayOfMonth, 7, 0)

                    val deep = Random.nextInt(15, 26).toFloat()
                    val rem = Random.nextInt(18, 28).toFloat()
                    val light = (100f - deep - rem).coerceIn(40f, 75f)

                    SleepCore(
                        userEmail = userEmail,
                        date = d,
                        sleepDuration = minutes, // minutos
                        sleepScore = score,
                        bedTime = bed,
                        wakeTime = wake,
                        deepSleepPercentage = deep,
                        remSleepPercentage = rem,
                        lightSleepPercentage = light,
                        awakeDuration = Random.nextLong(10, 41) // minutos
                    )
                }

                sleepDao.insertAll(demo)
                loadWeeklySummary(startDate)
            }
        }
    }

    fun navigateToPrevious() {
        val newDate = when (_viewMode.value) {
            ViewMode.WEEK -> _selectedDate.value.minus(7, DateTimeUnit.DAY)
            ViewMode.MONTH -> _selectedDate.value.minus(1, DateTimeUnit.MONTH)
        }
        _selectedDate.value = newDate
        if (_viewMode.value == ViewMode.WEEK) loadWeeklySummary(newDate)
    }

    fun navigateToNext() {
        val newDate = when (_viewMode.value) {
            ViewMode.WEEK -> _selectedDate.value.plus(7, DateTimeUnit.DAY)
            ViewMode.MONTH -> _selectedDate.value.plus(1, DateTimeUnit.MONTH)
        }
        _selectedDate.value = newDate
        if (_viewMode.value == ViewMode.WEEK) loadWeeklySummary(newDate)
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
