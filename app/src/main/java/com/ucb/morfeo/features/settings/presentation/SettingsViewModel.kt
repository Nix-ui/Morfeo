package com.ucb.morfeo.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.permissions.domain.usecase.GetPermissionsGrantedUseCase
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val jwtDataStore: JWTDataStore,
    getPermissionsGrantedUseCase: GetPermissionsGrantedUseCase
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
    }

    // 🔹 Guardar valores actualizados
    fun updateSleepTime(hour: Int,minute: Int) {
        viewModelScope.launch { settingsDataStore.saveSleepTime(hour,minute) }
    }

    fun updateWakeupTime(hour: Int,minute: Int) {
        viewModelScope.launch { settingsDataStore.saveWakeupTime(hour,minute) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.saveNotificationsEnabled(enabled) }
    }

    fun updateThemeMode(mode: Int) {
        viewModelScope.launch { settingsDataStore.saveThemeMode(mode) }
    }

    fun logout() {
        viewModelScope.launch {
            jwtDataStore.clearToken()
        }
    }
}
