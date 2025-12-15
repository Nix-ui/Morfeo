package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository
import kotlinx.datetime.LocalDateTime

class StopSleepTrackingUseCase(
    private val repository: ISleepAnalysisRepository
) {
    suspend operator fun invoke(
        sessionId:Int,
        wakeTime: LocalDateTime
    ):Result<SleepAnalysis>{
        return repository.stopSleepSession(sessionId,wakeTime)
    }
}