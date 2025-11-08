package com.ucb.morfeo.features.core.firabase.config.domain.usecase

import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository
import kotlinx.coroutines.flow.Flow

class AppInMaintenanceUseCase(
    private val firebaseConfigRepository: IFirebaseConfigRepository
) {
    suspend fun getInitialStatus(): Result<Boolean> {
        return firebaseConfigRepository.getMaintenanceStatus()
    }
    fun listenForUpdates(): Flow<Result<Boolean>> {
        return firebaseConfigRepository.listenToMaintenanceStatus()
    }
}