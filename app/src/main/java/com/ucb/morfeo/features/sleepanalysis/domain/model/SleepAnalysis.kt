package com.ucb.morfeo.features.sleepanalysis.domain.model

import com.ucb.morfeo.features.core.database.entity.AudioAnalysis
import kotlinx.datetime.LocalDateTime

data class SleepAnalysis(
    val id: Int,
    val userEmail: String,
    val date: String,
    val sleepDuration: Long,
    val sleepScore: Int,
    val bedTime: LocalDateTime,
    val wakeTime: LocalDateTime,
    val deepSleepPercentage: Float,
    val remSleepPercentage: Float,
    val lightSleepPercentage: Float,
    val awakeDuration: Long,
    val audioAnalysis: AudioAnalysis? = null
)
