package com.ucb.morfeo.features.week.domain.repository

import com.ucb.morfeo.features.home.domain.model.SleepRecordScoreModel
import com.ucb.morfeo.features.week.domain.model.DailySleepData
import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import kotlinx.datetime.LocalDate

interface WeeklyRepository {
    suspend fun getWeeklySummary(userEmail: String, weekStartDate: LocalDate? = null): WeeklySummary
    suspend fun getAllSleepData(userEmail: String): List<DailySleepData>

    suspend fun getLastSleepRecord(userEmail: String): Result<SleepRecordScoreModel>
}