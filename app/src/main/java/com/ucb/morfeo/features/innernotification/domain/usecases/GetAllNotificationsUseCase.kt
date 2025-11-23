package com.ucb.morfeo.features.innernotification.domain.usecases

import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotificationsUseCase(
    private val repository: INotificationRepository
) {
    fun invoke(): Flow<List<NotificationModel>> = repository.getAll()
}