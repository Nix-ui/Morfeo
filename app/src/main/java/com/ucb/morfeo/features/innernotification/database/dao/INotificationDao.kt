package com.ucb.morfeo.features.innernotification.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ucb.morfeo.features.innernotification.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface INotificationDao {
    @Query("Select * From notifications")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deletaAll()

    @Query("DELETE FROM notifications WHERE notification_id==:id")
    suspend fun deleteNotification(id:Int)

    @Query("SELECT * FROM notifications WHERE notification_id==:id")
    suspend fun getById(id: Int): NotificationEntity
}