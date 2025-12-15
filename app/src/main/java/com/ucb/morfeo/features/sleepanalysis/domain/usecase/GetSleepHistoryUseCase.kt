package com.ucb.morfeo.features.sleepanalysis.domain.usecase

import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository
import kotlinx.coroutines.flow.Flow

class GetSleepHistoryUseCase (
    private val repository: ISleepAnalysisRepository
){
    operator fun invoke(userEmail: String): Flow<List<SleepAnalysis>>{
        return repository.getSleepHistory(userEmail)
    }
}