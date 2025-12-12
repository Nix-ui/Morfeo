package com.ucb.morfeo.features.register.domain.usecase

import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.register.domain.model.RegisterUser
import com.ucb.morfeo.features.register.domain.repository.IRegisterRepository

class RegisterUseCase(private val repository: IRegisterRepository) {
    suspend operator fun invoke(registerUser: RegisterUser): Result<UserModel> {
        return repository.register(registerUser)
    }
}
