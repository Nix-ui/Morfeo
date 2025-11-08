package com.ucb.morfeo.features.core.firabase.config.domain.repository

import kotlinx.coroutines.flow.Flow

interface IFirebaseConfigRepository {
    suspend fun getMaintenanceStatus(): Result<Boolean>
    fun listenToMaintenanceStatus(): Flow<Result<Boolean>>
}