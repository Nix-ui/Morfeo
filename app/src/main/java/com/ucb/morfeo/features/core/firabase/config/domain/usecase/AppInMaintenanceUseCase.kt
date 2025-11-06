package com.ucb.morfeo.features.core.firabase.config.domain.usecase

import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository

class AppInMaintenanceUseCase(
    private val firebaseConfigRepository: IFirebaseConfigRepository
) {
    suspend fun invoke(): Result<Boolean> {
        return firebaseConfigRepository.getMaintenanceStatus()
    }
}