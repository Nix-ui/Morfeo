package com.ucb.morfeo.features.sleepanalysis.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface ISleepAnalysisRepository {
    suspend fun startSleepSession(
        userEmail:String,
        bedTime: LocalDateTime,
        scheduleWakeTime: LocalDateTime
    ): Result<SleepSession>
    suspend fun stopSleepSession(
        sessionId: Int,
        wakeTime: LocalDateTime
    ): Result<SleepAnalysis>
    suspend fun analyzeSleepAudio(
        audioFilePath: String,
        sessionId: Int
    ): Result<SleepAnalysis>
    fun getSleepHistory(userEmail: String): Flow<List<SleepAnalysis>>
    suspend fun cancelSleepSession(sessionId: Int): Result<Unit>
}