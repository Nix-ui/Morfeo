package com.ucb.morfeo.features.innernotification.domain.repository

import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    suspend fun create(notification: NotificationModel)
    fun getAll(): Flow<List<NotificationModel>>
    suspend fun getById(id: Int): Flow<NotificationModel?>
    suspend fun update(notification: NotificationModel)
    suspend fun delete(id: Int)
}