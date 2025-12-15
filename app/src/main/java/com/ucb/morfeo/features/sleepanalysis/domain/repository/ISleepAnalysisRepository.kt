package com.ucb.morfeo.features.sleepanalysis.domain.repository

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepSession
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface ISleepAnalysisRepository {
    suspend fun startSleepSession(
        userEmail:String,
        bedTime: LocalDateTime,
        scheduledWakeTime: LocalDateTime
    ): Result<SleepSession>
    suspend fun stopSleepSession(
        sessionId: Int,
        wakeTime: LocalDateTime
    ): Result<SleepAnalysis>
    suspend fun analyzeSleepAudio(
        audioFilePath: String,
        sessionId: Int
    ): Result<SleepAnalysis>
    fun getSleepHistory(
        userEmail: String
    ): Flow<List<SleepAnalysis>>
    suspend fun cancelSleepSession(sessionId: Int): Result<Unit>
    suspend fun getSleepStatistics(userEmail: String): SleepStatistics?
    data class SleepStatistics(
        val avgScore: Float,
        val avgDuration: Float,
        val avgDeepSleep: Float,
        val totalNights: Int
    )
}