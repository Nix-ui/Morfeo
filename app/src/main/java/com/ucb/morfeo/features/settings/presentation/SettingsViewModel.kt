package com.ucb.morfeo.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Estados de la interfaz
    private val _sleepTime = MutableStateFlow<String?>(null)
    val sleepTime: StateFlow<String?> = _sleepTime.asStateFlow()

    private val _wakeUpTime = MutableStateFlow<String?>(null)
    val wakeUpTime: StateFlow<String?> = _wakeUpTime.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _themeMode = MutableStateFlow(0) // 0 claro / 1 oscuro
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    init {
        loadSettings()
    }

    // 🔹 Cargar los valores guardados al iniciar
    private fun loadSettings() {
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
    }

    // 🔹 Guardar valores actualizados
    fun updateSleepTime(value: String) {
        viewModelScope.launch { settingsDataStore.saveSleepTime(value) }
    }

    fun updateWakeupTime(value: String) {
        viewModelScope.launch { settingsDataStore.saveWakeupTime(value) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.saveNotificationsEnabled(enabled) }
    }

    fun updateThemeMode(mode: Int) {
        viewModelScope.launch { settingsDataStore.saveThemeMode(mode) }
    }
}
