package com.ucb.morfeo.features.login.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
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
    suspend fun getActiveToken(): Result<JWT>{
        return try{
            val preferences = context.dataStore.data.first()
            val tokenString = preferences[JWT_TOKEN]
            if(tokenString.isNullOrBlank()){
                return Result.failure(Exception("No token found"))
            }
            val jwt = JWT(tokenString)
            if(jwt.isExpired(10)){
                clearToken()
                return Result.failure(Exception("Token expired"))
            }
            Result.success(jwt)
        }catch (e: Exception){
            Result.failure(Exception("Failed to decode session token: ${e.message}"))
        }
    }

    suspend fun getUserMail(): Result<String>{
        return try{
            val preferences = context.dataStore.data.first()
            val tokenString = preferences[JWT_TOKEN]
            if(tokenString.isNullOrBlank()){
                return Result.failure(Exception("No token found"))
            }
            val jwt = JWT(tokenString)
            if(jwt.isExpired(10)){
                clearToken()
                return Result.failure(Exception("Token expired"))
            }
            val email = jwt.getClaim("email").asString()
            if(email.isNullOrBlank()){
                return Result.failure(Exception("No email found"))
            }
            Result.success(email)
        }catch (e: Exception){
            Result.failure(Exception("Failed to decode session token: ${e.message}"))
        }
    }

    suspend fun getName(): Result<String>{
        return try{
            val preferences = context.dataStore.data.first()
            val tokenString = preferences[JWT_TOKEN]
            if(tokenString.isNullOrBlank()){
                return Result.failure(Exception("No token found"))
            }
            val jwt = JWT(tokenString)
            if(jwt.isExpired(10)){
                clearToken()
                return Result.failure(Exception("Token expired"))
            }
            val name = jwt.getClaim("name").asString()
            if(name.isNullOrBlank()){
                return Result.failure(Exception("No Name found"))
            }
            Result.success(name)
        }catch (e: Exception){
            Result.failure(Exception("Failed to decode session token: ${e.message}"))
        }
    }

    suspend fun clearToken(){
        context.dataStore.edit{
            it.remove(JWT_TOKEN)
        }
    }
}