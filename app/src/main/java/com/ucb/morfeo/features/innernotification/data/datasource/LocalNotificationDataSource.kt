package com.ucb.morfeo.features.innernotification.data.datasource

import com.ucb.morfeo.features.innernotification.data.mapper.toEntity
import com.ucb.morfeo.features.innernotification.data.mapper.toModel
import com.ucb.morfeo.features.innernotification.database.dao.INotificationDao
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalNotificationDataSource(
    val dao: INotificationDao
) {
    fun getAllNotifications(): Flow<List<NotificationModel>>{
        return dao.getAllNotifications().map { entityList->
            entityList.map { it.toModel() }
        }
    }
    suspend fun deleteAll(){
        dao.deletaAll()
    }
    suspend fun insertNotification(notification: NotificationModel){
        dao.insertNotification(notification.toEntity())
    }
    suspend fun updateNotification(notification: NotificationModel){
        dao.updateNotification(notification.toEntity())
    }
    suspend fun getById(id:Int): NotificationModel{
        return dao.getById(id).toModel()
    }
    suspend fun deleteNotification(id:Int){
        dao.deleteNotification(id)
    }
}