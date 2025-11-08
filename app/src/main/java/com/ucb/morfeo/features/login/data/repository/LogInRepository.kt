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
import com.ucb.morfeo.features.login.domain.model.LogInUser
import com.ucb.morfeo.features.login.domain.model.Peso
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.repository.ILogInRepository
import kotlinx.coroutines.tasks.await


class LogInRepository(
    private val logInDataStore: JWTDataStore
): ILogInRepository {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    override suspend fun logIn(logInUser: LogInUser): Result<UserModel> {
        return try{
            val authResult = firebaseAuth.signInWithEmailAndPassword(
                logInUser.email.value,
                logInUser.password.value
            ).await()
            val fireBaseUser = authResult.user
            if(fireBaseUser != null){
                val jwtToken = createSampleJwtTokenString(fireBaseUser,logInUser)
                logInDataStore.saveToken(jwtToken)
                Result.success(createUserModelFromFirebaseUser(fireBaseUser,logInUser))
            }else{
                Result.failure(Exception("User is null"))
            }
        }catch (e: Exception){
            Result.failure(Exception("Error de autenticacion: ${e.message}"))
        }
    }

    private fun createSampleJwtTokenString(firebaseUser: FirebaseUser, logInUser: LogInUser): String {
        // Este token fue generado con una herramienta online (como jwt.io) y es válido.
        // Payload:
        // {
        //   "sub": "some_firebase_uid",
        //   "name": "Some User Name",
        //   "email": "user@example.com",
        //   "iat": 1672531200, (timestamp de creación)
        //   "exp": 9999999999  (timestamp de expiración muy lejano para pruebas)
        // }
        // La firma usa el secreto "your-super-secret-key"
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        // Modificación: Se agrega el campo "email" al payload del JWT
        val payload = "eyJzdWIiOiIke3VzZXJfdWlkfSIsIm5hbWUiOiIke3VzZXJfbmFtZX0iLCJlbWFpbCI6IiR7dXNlcl9lbWFpbH0iLCJleHAiOjE4OTM0NTYwMDB9"
            .replace("\$${"uid"}", firebaseUser.uid)
            .replace("\$${"name"}", firebaseUser.displayName ?: "Morfeo")
            .replace("\$${"email"}", firebaseUser.email ?: logInUser.email.value)
        val signature = "your_generated_signature_part"
        return "$header.$payload."
    }

    private fun createUserModelFromFirebaseUser(firebaseUser: FirebaseUser, logInUser: LogInUser): UserModel{
        return UserModel(
            email = Email(firebaseUser.email ?: logInUser.email.value),
            nombre = firebaseUser.displayName ?: "User",
            edad = Edad(20),
            peso = Peso(70.0),
            altura = Altura(1.70),
            genero = Genero.MASCULINO
        )

    }
}