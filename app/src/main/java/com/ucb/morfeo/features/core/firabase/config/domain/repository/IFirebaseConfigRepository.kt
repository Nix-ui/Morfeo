package com.ucb.morfeo.features.core.firabase.config.domain.repository

interface IFirebaseConfigRepository {
    suspend fun getMaintenanceStatus(): Result<Boolean>
}