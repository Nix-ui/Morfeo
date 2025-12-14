package com.ucb.morfeo.features.addsleep.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class AddSleepFormState(
    val date: LocalDate,
    val bedHHmm: String = "23:00",
    val wakeHHmm: String = "07:00",
    val score: String = "75",
    val awakeMinutes: String = "20",
    val deep: String = "18",
    val rem: String = "22",
)

sealed class AddSleepUiState {
    object Idle : AddSleepUiState()
    object Saving : AddSleepUiState()
    data class Error(val message: String) : AddSleepUiState()
    object Saved : AddSleepUiState()
}

class AddSleepViewModel(
    private val jwtDataStore: JWTDataStore,
    private val sleepDao: SleepDao
) : ViewModel() {

    private val _form = MutableStateFlow(
        AddSleepFormState(
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    )
    val form: StateFlow<AddSleepFormState> = _form.asStateFlow()

    private val _ui = MutableStateFlow<AddSleepUiState>(AddSleepUiState.Idle)
    val ui: StateFlow<AddSleepUiState> = _ui.asStateFlow()

    fun setDate(date: LocalDate) { _form.value = _form.value.copy(date = date) }
    fun setBed(v: String) { _form.value = _form.value.copy(bedHHmm = v) }
    fun setWake(v: String) { _form.value = _form.value.copy(wakeHHmm = v) }
    fun setScore(v: String) { _form.value = _form.value.copy(score = v) }
    fun setAwake(v: String) { _form.value = _form.value.copy(awakeMinutes = v) }
    fun setDeep(v: String) { _form.value = _form.value.copy(deep = v) }
    fun setRem(v: String) { _form.value = _form.value.copy(rem = v) }

    fun save() {
        viewModelScope.launch {
            _ui.value = AddSleepUiState.Saving

            val f = _form.value

            val score = f.score.toIntOrNull()
            val awake = f.awakeMinutes.toLongOrNull()
            val deep = f.deep.toFloatOrNull()
            val rem = f.rem.toFloatOrNull()

            if (score == null || score !in 0..100) {
                _ui.value = AddSleepUiState.Error("Score inválido (0–100).")
                return@launch
            }
            if (awake == null || awake < 0) {
                _ui.value = AddSleepUiState.Error("Awake inválido (minutos).")
                return@launch
            }
            if (deep == null || rem == null || deep !in 0f..100f || rem !in 0f..100f) {
                _ui.value = AddSleepUiState.Error("Deep/REM inválidos (0–100).")
                return@launch
            }

            // ✅ Bed y Wake seguros (medianoche + fin de mes)
            val bed = parseHHmmToDateTime(f.date, f.bedHHmm)

            val wakeBase = parseHHmmToDateTime(f.date, f.wakeHHmm)
            val bedMin = bed.hour * 60 + bed.minute
            val wakeMin = wakeBase.hour * 60 + wakeBase.minute

            val wakeDate = if (wakeMin < bedMin) {
                f.date.plus(1, DateTimeUnit.DAY)
            } else {
                f.date
            }

            val wake = LocalDateTime(
                wakeDate.year,
                wakeDate.monthNumber,
                wakeDate.dayOfMonth,
                wakeBase.hour,
                wakeBase.minute
            )

            // ✅ Duración correcta incluso si cruza medianoche
            val duration = computeMinutesBetween(bed, wake)

            val light = (100f - deep - rem).coerceIn(0f, 100f)

            jwtDataStore.getUserMail().fold(
                onSuccess = { userEmail ->
                    val item = SleepCore(
                        id = 0,
                        userEmail = userEmail,
                        date = f.date,
                        sleepDuration = duration,
                        sleepScore = score,
                        bedTime = bed,
                        wakeTime = wake,
                        deepSleepPercentage = deep,
                        remSleepPercentage = rem,
                        lightSleepPercentage = light,
                        awakeDuration = awake
                    )
                    sleepDao.upsertByUserAndDate(item)
                    _ui.value = AddSleepUiState.Saved
                },
                onFailure = {
                    _ui.value = AddSleepUiState.Error("No se pudo obtener el email del usuario.")
                }
            )
        }
    }

    private fun parseHHmmToDateTime(date: LocalDate, hhmm: String): LocalDateTime {
        val parts = hhmm.trim().split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return LocalDateTime(
            date.year,
            date.monthNumber,
            date.dayOfMonth,
            h.coerceIn(0, 23),
            m.coerceIn(0, 59)
        )
    }

    private fun computeMinutesBetween(a: LocalDateTime, b: LocalDateTime): Long {
        val aMin = a.hour * 60 + a.minute
        var bMin = b.hour * 60 + b.minute
        if (bMin < aMin) bMin += 24 * 60
        return (bMin - aMin).toLong()
    }
}
