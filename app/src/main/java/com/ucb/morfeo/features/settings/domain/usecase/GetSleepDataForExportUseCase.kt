package com.ucb.morfeo.features.settings.domain.usecase

import com.ucb.morfeo.features.week.domain.model.DailySleepData
import com.ucb.morfeo.features.week.domain.repository.WeeklyRepository

class GetSleepDataForExportUseCase(private val weeklyRepository: WeeklyRepository) {
    suspend operator fun invoke(userEmail: String): List<DailySleepData> {
        return weeklyRepository.getAllSleepData(userEmail)
    }
}
