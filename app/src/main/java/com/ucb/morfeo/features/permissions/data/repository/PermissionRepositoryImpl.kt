package com.ucb.morfeo.features.permissions.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.ucb.morfeo.features.permissions.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "permissions")

class PermissionRepositoryImpl(private val context: Context) : PermissionRepository {

    private val permissionsGrantedKey = booleanPreferencesKey("permissions_granted")

    override val permissionsGranted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[permissionsGrantedKey] ?: false
        }

    override suspend fun setPermissionsGranted(granted: Boolean) {
        context.dataStore.edit {
            it[permissionsGrantedKey] = granted
        }
    }
}
