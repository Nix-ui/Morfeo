package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository

class CancelSleepSessionUseCase(
    private val repository: ISleepAnalysisRepository
) {
    suspend operator fun invoke(sessionId:Int): Result<Unit>{
        return repository.cancelSleepSession(sessionId)
    }
}