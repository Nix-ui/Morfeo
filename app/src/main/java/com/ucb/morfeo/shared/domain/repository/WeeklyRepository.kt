package com.ucb.morfeo.shared.domain.repository

import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import kotlinx.datetime.LocalDate

interface WeeklyRepository {
    suspend fun getWeeklySummary(weekStartDate: LocalDate? = null): WeeklySummary
}