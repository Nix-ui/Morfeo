package com.ucb.morfeo.features.week.domain.repository

import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import kotlinx.datetime.LocalDate

interface WeeklyRepository {
    suspend fun getWeeklySummary(userEmail: String, weekStartDate: LocalDate? = null): WeeklySummary
}