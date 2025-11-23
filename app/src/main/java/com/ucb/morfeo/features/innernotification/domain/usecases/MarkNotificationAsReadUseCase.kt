package com.ucb.morfeo.features.innernotification.domain.usecases

import com.ucb.morfeo.features.innernotification.domain.model.AppNotification
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository
import io.sentry.protocol.SentryException

class MarkNotificationAsReadUseCase(
    private val repository: INotificationRepository
) {
    suspend fun invoke(id:Int){
        try{
            var notification: NotificationModel? = null
            repository.getById(id).collect {
                it?.read=true
                notification = it
            }
            if(notification ==null){
                throw Exception("Notification Not Found")
            }else{
                repository.update(notification!!)
            }

        }catch (e: Exception){
            SentryException()
        }

    }
}