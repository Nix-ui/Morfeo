package com.ucb.morfeo.features.sleepanalysis.domain.model

import kotlinx.datetime.LocalDateTime

data class SleepSession(
    val id: Int,
    val userEmail: String,
    val bedTime: LocalDateTime,
    val scheduleWakeTime: LocalDateTime,
    val audioFilePath: String,
    val startTimestamp: Long = System.currentTimeMillis()
)
