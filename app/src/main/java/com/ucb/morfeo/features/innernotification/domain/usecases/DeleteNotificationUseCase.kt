package com.ucb.morfeo.features.innernotification.domain.usecases

import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository

class DeleteNotificationUseCase(
    private val repository: INotificationRepository
) {
    suspend fun invoke(id: Int){
        repository.delete(id)
    }
}