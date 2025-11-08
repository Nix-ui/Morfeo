package com.ucb.morfeo.features.login.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.login.domain.model.Altura
import com.ucb.morfeo.features.login.domain.model.Edad
import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.Genero
import com.ucb.morfeo.features.login.domain.model.Peso
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.repository.ICheckSessionRepository

class CheckSessionRepository(
    private val logInDataStore: JWTDataStore
) : ICheckSessionRepository {

    private val firebaseAuth: FirebaseAuth = Firebase.auth

    override suspend fun checkActiveSession(): Result<UserModel> {
        val tokenResult = logInDataStore.getActiveToken()
        return tokenResult.fold(
            onSuccess = {jwt->
                val userName = jwt.getClaim("name").asString() ?: "User"
                val userEmail = jwt.getClaim("email").asString()
                if(userEmail.isNullOrBlank() || !userEmail.contains("@")){
                    logInDataStore.clearToken()
                    return@fold Result.failure(Exception("Invalid email"))
                }
                Result.success(
                    UserModel(
                        email = Email(userEmail),
                        nombre = userName,
                        Edad(25),
                        genero = Genero.MASCULINO,
                        peso=Peso(70.0),
                        altura =Altura(1.70)
                    )
                )
            },
            onFailure = {
                Result.failure(it)
            }

        )
    }

    private fun createUserModelFromFirebaseUser(firebaseUser: FirebaseUser): UserModel {
        return UserModel(
            email = Email(firebaseUser.email ?: ""),
            nombre = firebaseUser.displayName ?: "User",
            edad = Edad(20),
            peso = Peso(70.0),
            altura = Altura(1.70),
            genero = Genero.MASCULINO
        )
    }
}
