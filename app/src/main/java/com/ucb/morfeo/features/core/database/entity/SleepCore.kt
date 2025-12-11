package com.ucb.morfeo.features.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "sleep_core")
data class SleepCore(
    @PrimaryKey
    val date: LocalDate,
    val sleepDuration: Long,
    val sleepScore: Int,
    val bedTime: LocalDateTime,
    val wakeTime: LocalDateTime,
    val deepSleepPercentage: Float,
    val remSleepPercentage: Float,
    val lightSleepPercentage: Float,
    val awakeDuration: Long
)