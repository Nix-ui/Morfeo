package com.ucb.morfeo.features.sleepanalysis.domain.model

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
    val audioAnalysis: DomainAudioAnalysis? = null
)
data class DomainAudioAnalysis(
    val peaks: List<DomainAudioPeak>,
    val snoreCount: Int,
    val movementCount: Int,
    val apneaEvents: Int
)

data class DomainAudioPeak(
    val timestamp: Long,
    val amplitude: Float,
    val frequency: Float,
    val peakType: PeakType
)
enum class PeakType{
    SNORE, MOVEMENT, APNEA, OTHER
}
