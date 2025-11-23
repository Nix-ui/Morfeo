package com.ucb.morfeo.features.welcome.data.database.entity

import androidx.compose.ui.text.font.FontWeight
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
import kotlin.uuid.Uuid

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name= "UUID")
    var uuid: Int = 0,

    @ColumnInfo(name = "user_email")
    var email: String,

    @ColumnInfo(name= "user_password")
    var password: String,

    @ColumnInfo(name="burn_date")
    var burnedDate: String,
    @ColumnInfo(name = "weight")
    var weight: Double,
    @ColumnInfo(name = "height")
    var height: Double
)
