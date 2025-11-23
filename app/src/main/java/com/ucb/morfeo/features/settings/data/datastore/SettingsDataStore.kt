package com.ucb.morfeo.features.settings.data.datastore

import androidx.datastore.preferences.core.edit

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 🔹 Crea la instancia de DataStore
val Context.settingsDataStore by preferencesDataStore(name = "settings_preferences")

class SettingsDataStore(private val context: Context) {

    companion object {
        val SLEEP_TIME = longPreferencesKey("sleep_time")
        val WAKEUP_TIME = longPreferencesKey("wakeup_time")        // hora de despertar
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME_MODE = intPreferencesKey("theme_mode")             // 0=light,1=dark

        private val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        private val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    }
    suspend fun saveSleepTime(hour: Int,minute: Int ) {
        val timeAsLong = hour * 100L + minute
        context.settingsDataStore.edit { prefs ->
            prefs[SLEEP_TIME] = timeAsLong
        }
    }
    fun getSleepTime(): Flow<Pair<Int, Int>> = context.settingsDataStore.data
        .map { prefs ->
            val time = prefs[SLEEP_TIME] ?: 2300L
            (time / 100).toInt() to (time % 100).toInt()
        }

    suspend fun saveWakeupTime(hour: Int,minute: Int ) {
        val timeAsLong = hour * 100L + minute
        context.settingsDataStore.edit { prefs ->
            prefs[WAKEUP_TIME] = timeAsLong
        }
    }

    fun getWakeupTime(): Flow<Pair<Int, Int>> = context.settingsDataStore.data
        .map { prefs ->
            val time = prefs[WAKEUP_TIME] ?: 2300L
            (time / 100).toInt() to (time % 100).toInt()
        }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun isNotificationsEnabled(): Flow<Boolean> =
        context.settingsDataStore.data.map { prefs ->
            prefs[NOTIFICATIONS_ENABLED] ?: true
        }
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
