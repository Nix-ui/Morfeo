package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository

class AnalizeSleepAudioUseCase(
    private val repository: ISleepAnalysisRepository
) {
    suspend operator fun invoke(sessionId:Int ,audioFilePath: String): Result<SleepAnalysis>{
        return repository.analyzeSleepAudio(sessionId = sessionId, audioFilePath = audioFilePath)
    }
}