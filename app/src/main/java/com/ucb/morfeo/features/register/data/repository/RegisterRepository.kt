package com.ucb.morfeo.features.register.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.login.domain.model.Altura
import com.ucb.morfeo.features.login.domain.model.Edad
import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.Genero
import com.ucb.morfeo.features.login.domain.model.Peso
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.register.domain.model.RegisterUser
import com.ucb.morfeo.features.register.domain.repository.IRegisterRepository
import kotlinx.coroutines.tasks.await

class RegisterRepository(
    private val logInDataStore: JWTDataStore
) : IRegisterRepository {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    override suspend fun register(registerUser: RegisterUser): Result<UserModel> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                registerUser.email.value,
                registerUser.password.value
            ).await()

            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(registerUser.nombre)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val jwtToken = "sample.jwt.token"
                logInDataStore.saveToken(jwtToken)

                val userModel = UserModel(
                    email = Email(firebaseUser.email!!),
                    nombre = firebaseUser.displayName!!,
                    edad = registerUser.edad,
                    peso = registerUser.peso,
                    altura = registerUser.altura,
                    genero = Genero.OTRO
                )
                Result.success(userModel)
            } else {
                Result.failure(Exception("Error en el registro, el usuario es nulo."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error en el registro: ${e.message}"))
        }
    }
}
