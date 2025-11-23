package com.ucb.morfeo.features.innernotification.domain.usecases

import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository

class RecivedNotificationUseCase(
    private val repository: INotificationRepository
) {
    suspend fun invoke(notification: NotificationModel){
        repository.create(notification)
    }
}