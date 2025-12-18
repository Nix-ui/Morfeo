package com.ucb.morfeo.features.home.domain.usecase

import com.ucb.morfeo.features.home.domain.model.SleepRecordScoreModel
import com.ucb.morfeo.features.week.domain.repository.WeeklyRepository

class GetLastRecord(
    private val repository: WeeklyRepository
) {
    suspend operator fun invoke(userEmail: String): Result<SleepRecordScoreModel> {
        return repository.getLastSleepRecord(userEmail)
    }
}