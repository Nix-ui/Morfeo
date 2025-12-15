package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepSession
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository
import kotlinx.datetime.LocalDateTime

class StartSleepTrackingUseCase(
    private val repository: ISleepAnalysisRepository
) {
    suspend operator fun invoke(
        userEmail: String,
        bedTime: LocalDateTime,
        scheduleWakeTime: LocalDateTime
    ): Result<SleepSession>{
        return repository.startSleepSession(userEmail,bedTime,scheduleWakeTime)
    }

}