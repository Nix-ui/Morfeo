package com.ucb.morfeo.features.innernotification.data.repository

import com.ucb.morfeo.features.innernotification.data.datasource.LocalNotificationDataSource
import com.ucb.morfeo.features.innernotification.data.datastore.local.LocalNotificationDataStore
import com.ucb.morfeo.features.innernotification.domain.model.AppNotification
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(
    private val localNotificationDataSource: LocalNotificationDataSource
): INotificationRepository {
    override suspend fun create(notification: NotificationModel) {
        localNotificationDataSource.insertNotification(notification)
    }

    override  fun getAll(): Flow<List<NotificationModel>> {
        return localNotificationDataSource.getAllNotifications()
    }

    override suspend fun getById(id: Int): Flow<NotificationModel?> {
        return flow {
            emit(localNotificationDataSource.getById(id))
        }
    }

    override suspend fun update(notification: NotificationModel) {
        localNotificationDataSource.updateNotification(notification)
    }

    override suspend fun delete(id: Int) {
        localNotificationDataSource.deleteNotification(id)
    }
}