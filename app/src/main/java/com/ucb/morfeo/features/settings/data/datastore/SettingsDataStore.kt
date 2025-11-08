package com.ucb.morfeo.features.settings.data.datastore

import androidx.datastore.preferences.core.edit

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 🔹 Crea la instancia de DataStore
val Context.settingsDataStore by preferencesDataStore(name = "settings_preferences")

class SettingsDataStore(private val context: Context) {

    companion object {
        // Claves para cada preferencia
        val SLEEP_TIME = stringPreferencesKey("sleep_time")          // hora de dormir
        val WAKEUP_TIME = stringPreferencesKey("wakeup_time")        // hora de despertar
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME_MODE = intPreferencesKey("theme_mode")             // 0=light,1=dark
    }

    // 🔸 Guardar hora de dormir
    suspend fun saveSleepTime(value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[SLEEP_TIME] = value
        }
    }

    // 🔸 Leer hora de dormir
    fun getSleepTime(): Flow<String?> = context.settingsDataStore.data.map { prefs ->
        prefs[SLEEP_TIME]
    }

    // 🔸 Guardar hora de despertar
    suspend fun saveWakeupTime(value: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[WAKEUP_TIME] = value
        }
    }

    fun getWakeupTime(): Flow<String?> = context.settingsDataStore.data.map { prefs ->
        prefs[WAKEUP_TIME]
    }

    // 🔸 Guardar si las notificaciones están activadas
    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun isNotificationsEnabled(): Flow<Boolean> =
        context.settingsDataStore.data.map { prefs ->
            prefs[NOTIFICATIONS_ENABLED] ?: true
        }

    // 🔸 Guardar modo de tema
    suspend fun saveThemeMode(mode: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    fun getThemeMode(): Flow<Int> =
        context.settingsDataStore.data.map { prefs ->
            prefs[THEME_MODE] ?: 0
        }
}
