package com.ucb.morfeo.features.login.domain.usecase

import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.repository.ICheckSessionRepository

class CheckSessionUseCase(
    private val checkSessionRepository: ICheckSessionRepository
) {
    suspend fun invoke(): Result<UserModel> {
        return checkSessionRepository.checkActiveSession()
    }
}
