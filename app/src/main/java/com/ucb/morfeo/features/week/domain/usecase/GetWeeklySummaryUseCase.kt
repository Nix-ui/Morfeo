package com.ucb.morfeo.features.week.domain.usecase

import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import com.ucb.morfeo.features.week.domain.repository.WeeklyRepository
import kotlinx.datetime.LocalDate

class GetWeeklySummaryUseCase(
    private val weeklyRepository: WeeklyRepository
) {
    suspend operator fun invoke(weekStartDate: LocalDate? = null): Result<WeeklySummary> {
        return try {
            val summary = weeklyRepository.getWeeklySummary(weekStartDate)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}