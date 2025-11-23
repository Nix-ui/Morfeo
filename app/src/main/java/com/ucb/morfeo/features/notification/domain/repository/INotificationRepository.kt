package com.ucb.morfeo.features.notification.domain.repository

import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    suspend fun getNotification(): Flow<String>
}