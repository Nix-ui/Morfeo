package com.ucb.morfeo.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.permissions.domain.usecase.GetPermissionsGrantedUseCase
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val sleepTime: Pair<Int, Int> = 0 to 0,
    val wakeUpTime: Pair<Int, Int> = 0 to 0,
    val notificationsEnabled: Boolean = true,
    val themeMode: Int = 0,
    val arePermissionsGranted: Boolean = false
)

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val jwtDataStore: JWTDataStore,
    getPermissionsGrantedUseCase: GetPermissionsGrantedUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.getSleepTime(),
        settingsDataStore.getWakeupTime(),
        settingsDataStore.isNotificationsEnabled(),
        settingsDataStore.getThemeMode(),
        getPermissionsGrantedUseCase()
    ) { sleepTime, wakeupTime, notificationsEnabled, themeMode, arePermissionsGranted ->
        SettingsUiState(
            sleepTime = sleepTime,
            wakeUpTime = wakeupTime,
            notificationsEnabled = notificationsEnabled,
            themeMode = themeMode,
            arePermissionsGranted = arePermissionsGranted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

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

    fun logout() {
        viewModelScope.launch {
            jwtDataStore.clearToken()
        }
    }
}
