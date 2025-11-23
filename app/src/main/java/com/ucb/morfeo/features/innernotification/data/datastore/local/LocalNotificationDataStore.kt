package com.ucb.morfeo.features.innernotification.data.datastore.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ucb.morfeo.features.innernotification.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalNotificationDataStore(
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name= "notifications")
    private val NOTIFICATIONS_KEY = stringPreferencesKey("notifications_json")
    private val json = Json {
        encodeDefaults=true;
        ignoreUnknownKeys=true
    }
    suspend fun saveAll(notifications: List<AppNotification>){
        val str = json.encodeToString(notifications)
        context.dataStore.edit { prefs->
            prefs[NOTIFICATIONS_KEY]= str
        }
    }
    val allNotification: Flow<List<AppNotification>> = context.dataStore.data.map {prefs->
        prefs[NOTIFICATIONS_KEY]?.let {str->
            try{
                json.decodeFromString<List<AppNotification>>(str)
            }catch (e: Exception){
                emptyList()
            }
        } ?: emptyList()
    }
    suspend fun create(notification: AppNotification){
        val current = allNotification.first()
        saveAll(current+notification)
    }
    suspend fun update(notification: AppNotification){
        val current = allNotification.first()
        saveAll(current.map { if(it.id == notification.id) notification else it })
    }
    suspend fun delete(id:Int){
        val current = allNotification.first()
        saveAll(current.filterNot { it.id === id })
    }
    suspend fun getById(id:Int): AppNotification?{
        return allNotification.first().firstOrNull{
            it.id == id
        }
    }
}