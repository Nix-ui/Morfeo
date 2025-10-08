package com.ucb.morfeo.features.notification.data.repository

import com.ucb.morfeo.features.notification.data.datasource.RealTimeRemoteDataSource
import com.ucb.morfeo.features.notification.domain.repository.INotificationRepository
import com.ucb.morfeo.features.welcome.domain.model.UserModel
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    val remoteDataSource: RealTimeRemoteDataSource
): INotificationRepository {
    override suspend fun getNotification(): Flow<String> {
        return remoteDataSource.notificationEvents()
    }
}