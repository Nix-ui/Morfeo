package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository

class AnalyzeSleepAudioUseCase(
    private val repository: ISleepAnalysisRepository
) {
    suspend fun invoke(
        audioFilePath: String,
        sessionId: Int
    ): Result<SleepAnalysis>{
        return repository.analyzeSleepAudio(audioFilePath, sessionId)
    }
}