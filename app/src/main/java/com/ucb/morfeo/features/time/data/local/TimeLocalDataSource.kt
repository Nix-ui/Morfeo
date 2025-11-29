package com.ucb.helpet.features.time.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "time_settings")

class TimeLocalDataSource(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val SERVER_TIME_KEY = longPreferencesKey("server_time_snapshot")
        val ELAPSED_REALTIME_KEY = longPreferencesKey("elapsed_realtime_snapshot")
    }

    suspend fun saveTimeSnapshot(serverTime: Long, elapsedRealtime: Long) {
        dataStore.edit { settings ->
            settings[SERVER_TIME_KEY] = serverTime
            settings[ELAPSED_REALTIME_KEY] = elapsedRealtime
        }
    }

    suspend fun getTimeSnapshot(): Pair<Long, Long>? {
        return dataStore.data.map { preferences ->
            val serverTime = preferences[SERVER_TIME_KEY]
            val elapsedRealtime = preferences[ELAPSED_REALTIME_KEY]
            if (serverTime != null && elapsedRealtime != null) {
                Pair(serverTime, elapsedRealtime)
            } else {
                null
            }
        }.first()
    }
}