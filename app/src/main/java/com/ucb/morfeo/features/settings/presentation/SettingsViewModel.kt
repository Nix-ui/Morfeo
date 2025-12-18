package com.ucb.morfeo.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.permissions.domain.usecase.GetPermissionsGrantedUseCase
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import com.ucb.morfeo.features.settings.domain.model.SleepDataExport
import com.ucb.morfeo.features.settings.domain.usecase.GetSleepDataForExportUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.datetime.minus
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val jwtDataStore: JWTDataStore,
    getPermissionsGrantedUseCase: GetPermissionsGrantedUseCase,
    private val getSleepDataForExportUseCase: GetSleepDataForExportUseCase
) : ViewModel() {

    // Estados de la interfaz
    private val _sleepTime = MutableStateFlow<Pair<Int, Int>>(0 to 0)
    val sleepTime: StateFlow<Pair<Int, Int>> = _sleepTime.asStateFlow()

    private val _wakeUpTime = MutableStateFlow<Pair<Int, Int>>(0 to 0)
    val wakeUpTime: StateFlow<Pair<Int, Int>> = _wakeUpTime.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _themeMode = MutableStateFlow(0) // 0 claro / 1 oscuro
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    private val _sleepGoal = MutableStateFlow(8)
    val sleepGoal: StateFlow<Int> = _sleepGoal.asStateFlow()

    private val _exportResult = MutableSharedFlow<Result<String>>()
    val exportResult = _exportResult.asSharedFlow()

    init {
        loadSettings(getPermissionsGrantedUseCase)
    }

    // 🔹 Cargar los valores guardados al iniciar
    private fun loadSettings(getPermissionsGrantedUseCase: GetPermissionsGrantedUseCase) {
        viewModelScope.launch {
            settingsDataStore.getSleepTime().collect { _sleepTime.value = it }
        }
        viewModelScope.launch {
            settingsDataStore.getWakeupTime().collect { _wakeUpTime.value = it }
        }
        viewModelScope.launch {
            settingsDataStore.isNotificationsEnabled().collect { _notificationsEnabled.value = it }
        }
        viewModelScope.launch {
            settingsDataStore.getThemeMode().collect { _themeMode.value = it }
        }
        viewModelScope.launch {
            getPermissionsGrantedUseCase().collect { _permissionsGranted.value = it }
        }
        viewModelScope.launch {
            settingsDataStore.getSleepGoal().collect { _sleepGoal.value = it }
        }
    }

    // 🔹 Guardar valores actualizados
    fun updateSleepTime(hour: Int, minute: Int) {
        viewModelScope.launch { settingsDataStore.saveSleepTime(hour, minute) }
    }

    fun updateWakeupTime(hour: Int, minute: Int) {
        viewModelScope.launch { settingsDataStore.saveWakeupTime(hour, minute) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.saveNotificationsEnabled(enabled) }
    }

    fun updateThemeMode(mode: Int) {
        viewModelScope.launch { settingsDataStore.saveThemeMode(mode) }
    }

    fun updateSleepGoal(hours: Int) {
        viewModelScope.launch { settingsDataStore.saveSleepGoal(hours) }
    }

    fun exportData(dateRange: String, dataTypes: List<String>) {
        viewModelScope.launch {
            jwtDataStore.getUserMail().onSuccess { userEmail ->
                try {
                    // 1. Obtener todos los datos de sueño
                    val allSleepData = getSleepDataForExportUseCase(userEmail)

                    // 2. Filtrar por rango de fechas
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val filteredByDate = when (dateRange) {
                        "Última semana" -> allSleepData.filter { it.date >= today.minus(kotlinx.datetime.DatePeriod(days = 7)) }
                        "Último mes" -> allSleepData.filter { it.date >= today.minus(kotlinx.datetime.DatePeriod(months = 1)) }
                        else -> allSleepData
                    }

                    // 3. Mapear a una lista de objetos SleepDataExport
                    val dataToSerialize = filteredByDate.map { dailyData ->
                        SleepDataExport(
                            date = dailyData.date.toString(),
                            sleepDuration = if (dataTypes.contains("Horas de sueño")) dailyData.sleepDuration.inWholeMilliseconds else null,
                            sleepScore = if (dataTypes.contains("Puntuación de sueño")) dailyData.sleepScore else null
                        )
                    }

                    // 4. Convertir a JSON
                    val jsonString = Json.encodeToString(dataToSerialize)

                    // 5. Emitir el resultado para que la UI lo guarde
                    _exportResult.emit(Result.success(jsonString))
                } catch (e: Exception) {
                    _exportResult.emit(Result.failure(e))
                }

            }.onFailure { exception ->
                _exportResult.emit(Result.failure(exception))
            }
        }
    }

    fun deleteAllData() {
        // TODO: Implementar lógica de borrado de datos
    }

    fun logout() {
        viewModelScope.launch {
            jwtDataStore.clearToken()
        }
    }
}
