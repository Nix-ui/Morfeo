package com.ucb.morfeo.features.notification.domain.usecase

import com.ucb.morfeo.features.notification.domain.repository.INotificationRepository
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import kotlinx.coroutines.flow.Flow

class FetchNotificationCase(
    val repository: INotificationRepository
) {
    suspend fun invoke(): Flow<String> {
        return repository.getNotification()
    }
}