package com.ucb.morfeo.features.login.domain.repository

import com.ucb.morfeo.features.login.domain.model.LogInUser
import com.ucb.morfeo.features.login.domain.model.UserModel

interface ILogInRepository {
    suspend fun logIn(logInUser: LogInUser): Result<UserModel>
}