package com.ucb.morfeo.features.week.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class WeeklySummary(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val dailyData: List<DailySleepData>,
    val averageSleepDuration: Duration,
    val averageSleepScore: Int,
    val consistencyScore: Float,
    val comparisonWithPreviousWeek: Float,
    val bestSleepDay: DailySleepData?,
    val worstSleepDay: DailySleepData?
)