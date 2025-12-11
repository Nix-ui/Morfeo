package com.ucb.morfeo.features.innernotification.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ucb.morfeo.navigation.Screen

@Entity(tableName= "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="notification_id")
    var id: Int,

    @ColumnInfo(name= "title")
    var title: String,

    @ColumnInfo(name=  "content")
    var content:String,

    @ColumnInfo(name = "path", defaultValue = "/home")
    var path:String="/home",

    @ColumnInfo(name= "create_date")
    var createDate: Long,

    @ColumnInfo(name= "read")
    var read: Boolean = false,

    @ColumnInfo(name= "readed_date")
    var readedDate: Long? = null
)