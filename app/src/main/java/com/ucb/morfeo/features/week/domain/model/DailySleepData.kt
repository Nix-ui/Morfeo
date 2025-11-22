package com.ucb.morfeo.features.week.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class DailySleepData(
    val date: LocalDate,
    val sleepDuration: Duration,
    val sleepScore: Int,
    val bedTime: LocalDateTime,
    val wakeTime: LocalDateTime,
    val deepSleepPercentage: Float,
    val remSleepPercentage: Float,
    val lightSleepPercentage: Float
)