package com.ucb.morfeo.features.login.data.repository

import android.util.Log
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
                val name = logInDataStore.getName()
                name.fold(
                    onSuccess = {
                        Result.success(createUserModelFromFirebaseUser(fireBaseUser,logInUser,it))
                    },
                    onFailure = {
                        Result.success(createUserModelFromFirebaseUser(fireBaseUser,logInUser,"Morfi"))
                    }
                )
            }else{
                Result.failure(Exception("User is null"))
            }
        }catch (e: Exception){
            Result.failure(Exception("Error de autenticacion: ${e.message}"))
        }
    }

    private  suspend fun createSampleJwtTokenString(firebaseUser: FirebaseUser, logInUser: LogInUser): String {
        val data = firebaseUser.getIdToken(true).await().token ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik1vcmZlbyIsImVtYWlsIjoibW9yZmVvQHVjYi5lZHUuYm8iLCJpYXQiOjE1MTYyMzkwMjJ9.-mCtaDlhaRXlSV43Di4BQdTcbklOBmCLdz44esWOeYs"
        Log.d("Token",data)
        return data
    }

    private fun createUserModelFromFirebaseUser(firebaseUser: FirebaseUser, logInUser: LogInUser,name: String): UserModel{
        return UserModel(
            email = Email(firebaseUser.email ?: logInUser.email.value),
            nombre = firebaseUser.displayName ?: name,
            edad = Edad(20),
            peso = Peso(70.0),
            altura = Altura(1.70),
            genero = Genero.MASCULINO
        )

    }
}