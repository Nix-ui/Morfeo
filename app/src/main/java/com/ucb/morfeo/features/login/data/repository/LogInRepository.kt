package com.ucb.morfeo.features.login.data.repository

import androidx.datastore.dataStore
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.login.domain.model.Altura
import com.ucb.morfeo.features.login.domain.model.Edad
import com.ucb.morfeo.features.login.domain.model.Genero
import com.ucb.morfeo.features.login.domain.model.LogInUser
import com.ucb.morfeo.features.login.domain.model.Peso
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.repository.ILogInRepository

class LogInRepository(
    private val logInDataStore: JWTDataStore
): ILogInRepository {
    override suspend fun logIn(logInUser: LogInUser): Result<UserModel> {
        if(logInDataStore.getToken().isSuccess){
            return Result.success(UserModel(
                email = logInUser.email,
                nombre = "Pedro",
                edad = Edad(20),
                genero = Genero.MASCULINO,
                altura = Altura(180.0),
                peso = Peso(70.0)
            ))
        }
         if(logInUser.password.value == logInUser.password.value){
             logInDataStore.saveToken(logInUser.email.value)
             return Result.success(UserModel(
                email = logInUser.email,
                nombre = "Juan",
                edad = Edad(20),
                genero = Genero.MASCULINO,
                altura = Altura(180.0),
                peso = Peso(70.0)
            ))
        }else{
            return Result.failure(Exception("Contraseña incorrecta"))
        }
    }
}