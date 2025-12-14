package com.ucb.morfeo.features.register.domain.repository

import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.register.domain.model.RegisterUser

interface IRegisterRepository {
    suspend fun register(registerUser: RegisterUser): Result<UserModel>
}
