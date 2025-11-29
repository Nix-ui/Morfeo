package com.ucb.morfeo.features.login.domain.usecase

import com.ucb.morfeo.features.login.domain.model.LogInUser
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.repository.ILogInRepository

class FetchLogInUserUseCase(
    private val logInRepository: ILogInRepository
) {
    suspend fun invoke(logInUser: LogInUser) : Result<UserModel>{
        return logInRepository.logIn(logInUser)
    }
}