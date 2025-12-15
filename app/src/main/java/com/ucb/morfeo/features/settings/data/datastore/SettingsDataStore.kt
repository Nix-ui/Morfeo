package com.ucb.morfeo.features.settings.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 🔹 Crea la instancia de DataStore
val Context.settingsDataStore by preferencesDataStore(name = "settings_preferences")

class SettingsDataStore(private val context: Context) {

    companion object {
        val SLEEP_TIME = longPreferencesKey("sleep_time")
        val WAKEUP_TIME = longPreferencesKey("wakeup_time")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME_MODE = intPreferencesKey("theme_mode") // 0=light,1=dark
        val SLEEP_GOAL_HOURS = intPreferencesKey("sleep_goal_hours") // Meta de sueño en horas
    }

    suspend fun saveSleepTime(hour: Int, minute: Int) {
        val timeAsLong = hour * 100L + minute
        context.settingsDataStore.edit {
            it[SLEEP_TIME] = timeAsLong
        }
    }

    fun getSleepTime(): Flow<Pair<Int, Int>> = context.settingsDataStore.data
        .map { prefs ->
            val time = prefs[SLEEP_TIME] ?: 2200L // Default 22:00
            (time / 100).toInt() to (time % 100).toInt()
        }

    suspend fun saveWakeupTime(hour: Int, minute: Int) {
        val timeAsLong = hour * 100L + minute
        context.settingsDataStore.edit {
            it[WAKEUP_TIME] = timeAsLong
        }
    }

    fun getWakeupTime(): Flow<Pair<Int, Int>> = context.settingsDataStore.data
        .map { prefs ->
            val time = prefs[WAKEUP_TIME] ?: 700L // Default 07:00
            (time / 100).toInt() to (time % 100).toInt()
        }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit {
            it[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun isNotificationsEnabled(): Flow<Boolean> = context.settingsDataStore.data.map {
        it[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun saveThemeMode(mode: Int) {
        context.settingsDataStore.edit {
            it[THEME_MODE] = mode
        }
    }

    fun getThemeMode(): Flow<Int> = context.settingsDataStore.data.map {
        it[THEME_MODE] ?: 0
    }

    suspend fun saveSleepGoal(hours: Int) {
        context.settingsDataStore.edit {
            it[SLEEP_GOAL_HOURS] = hours
        }
    }

    fun getSleepGoal(): Flow<Int> = context.settingsDataStore.data.map {
        it[SLEEP_GOAL_HOURS] ?: 8 // Default 8 hours
    }
}
