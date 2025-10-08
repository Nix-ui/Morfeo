package com.ucb.morfeo.features.login.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name= "login")
class JWTDataStore(
    private val context: Context
){
    companion object{
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
    }
    suspend fun saveToken(token: String){
        context.dataStore.edit{
            it[JWT_TOKEN] = token
        }
    }
    suspend fun getToken(): Result<String?>{
        val preferences = context.dataStore.data.first()
        return if(preferences.contains(JWT_TOKEN)){
            Result.success(preferences[JWT_TOKEN])
        }else{
            Result.failure(Exception("Token not found"))
        }
    }
}