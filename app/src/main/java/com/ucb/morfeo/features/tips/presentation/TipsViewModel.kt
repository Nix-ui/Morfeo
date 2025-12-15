package com.ucb.morfeo.features.tips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.tips.domain.model.Tip
import com.ucb.morfeo.features.tips.domain.usecase.GeneratePersonalizedTipsUseCase
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
import kotlinx.datetime.toJavaLocalDate

class TipsViewModel(
    private val jwtDataStore: JWTDataStore,
    private val sleepDao: SleepDao,
    private val getWeeklySummary: GetWeeklySummaryUseCase,
    private val generateTips: GeneratePersonalizedTipsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TipsUiState>(TipsUiState.Loading)
    val state: StateFlow<TipsUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = TipsUiState.Loading

            val emailResult = jwtDataStore.getUserMail()
            if (emailResult.isFailure) {
                _state.value = TipsUiState.Error("No se pudo obtener el email del usuario.")
                return@launch
            }
            val userEmail = emailResult.getOrThrow()

            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val weekStart = startOfWeek(today)
            val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)

            val weeklyRes = getWeeklySummary(userEmail, weekStart)
            val weekly: WeeklySummary? = weeklyRes.getOrNull()

            // Intentamos: hoy -> si no existe, buscamos último registro de la semana
            val todaySleep: SleepCore? = sleepDao.getSleepByDate(userEmail, today)
                ?: sleepDao.getSleepSessionsBetween(userEmail, weekStart, weekEnd).lastOrNull()

            val tips: List<Tip> = generateTips.execute(todaySleep, weekly)

            _state.value = TipsUiState.Success(
                date = (todaySleep?.date ?: today),
                today = todaySleep,
                weekly = weekly,
                tips = tips
            )
        }
    }

    private fun startOfWeek(date: LocalDate): LocalDate {
        val iso = date.toJavaLocalDate().dayOfWeek.value // lunes=1
        return date.minus((iso - 1).toLong(), DateTimeUnit.DAY)
    }

    init {
        load()
    }
}

sealed class TipsUiState {
    data object Loading : TipsUiState()
    data class Error(val message: String) : TipsUiState()
    data class Success(
        val date: LocalDate,
        val today: SleepCore?,
        val weekly: WeeklySummary?,
        val tips: List<Tip>
    ) : TipsUiState()
}
