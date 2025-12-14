package com.ucb.morfeo.features.week.data.repository

import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.week.domain.model.DailySleepData
import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import com.ucb.morfeo.features.week.domain.usecase.CalculateConsistencyUseCase
import com.ucb.morfeo.features.week.domain.repository.WeeklyRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class WeeklyRepositoryImpl(
    private val sleepDao: SleepDao,
    private val calculateConsistency: CalculateConsistencyUseCase
) : WeeklyRepository {

    override suspend fun getWeeklySummary(userEmail: String, weekStartDate: LocalDate?): WeeklySummary {
        val startDate = weekStartDate ?: getCurrentWeekStart()
        val endDate = startDate.plusDays(6)

        val currentWeekData = sleepDao.getSleepSessionsBetween(userEmail, startDate, endDate)
            .map { it.toDailySleepData() }

        val previousWeekStart = startDate.minusDays(7)
        val previousWeekEnd = endDate.minusDays(7)
        val previousWeekData = sleepDao.getSleepSessionsBetween(userEmail, previousWeekStart, previousWeekEnd)
            .map { it.toDailySleepData() }

        val consistency = calculateConsistency(currentWeekData)
        val comparison = calculateWeekComparison(currentWeekData, previousWeekData)

        return WeeklySummary(
            weekStartDate = startDate,
            weekEndDate = endDate,
            dailyData = currentWeekData,
            averageSleepDuration = calculateAverageSleepDuration(currentWeekData),
            averageSleepScore = calculateAverageSleepScore(currentWeekData),
            consistencyScore = consistency.consistencyScore,
            comparisonWithPreviousWeek = comparison,
            bestSleepDay = currentWeekData.maxByOrNull { it.sleepScore },
            worstSleepDay = currentWeekData.minByOrNull { it.sleepScore }
        )
    }

    private fun getCurrentWeekStart(): LocalDate {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfWeek = today.dayOfWeek.ordinal
        return today.minusDays(dayOfWeek.toLong())
    }

    private fun calculateAverageSleepDuration(dailyData: List<DailySleepData>): Duration {
        if (dailyData.isEmpty()) return Duration.ZERO

        val totalSeconds = dailyData.sumOf { it.sleepDuration.inWholeSeconds }
        val averageSeconds = totalSeconds / dailyData.size

        return averageSeconds.seconds
    }

    private fun calculateAverageSleepScore(dailyData: List<DailySleepData>): Int {
        if (dailyData.isEmpty()) return 0
        return (dailyData.sumOf { it.sleepScore } / dailyData.size).toInt()
    }

    private fun calculateWeekComparison(
        currentWeek: List<DailySleepData>,
        previousWeek: List<DailySleepData>
    ): Float {
        if (previousWeek.isEmpty() || currentWeek.isEmpty()) return 0f

        val currentAvgScore = calculateAverageSleepScore(currentWeek)
        val previousAvgScore = calculateAverageSleepScore(previousWeek)

        if (previousAvgScore == 0) return 0f

        return ((currentAvgScore - previousAvgScore).toFloat() / previousAvgScore) * 100
    }
}

// Extension functions para LocalDate
private fun LocalDate.plusDays(days: Long): LocalDate {
    return this.plus(days, DateTimeUnit.DAY)
}

private fun LocalDate.minusDays(days: Long): LocalDate {
    return this.minus(days, DateTimeUnit.DAY)
}

private fun com.ucb.morfeo.features.core.database.entity.SleepCore.toDailySleepData(): DailySleepData {
    return DailySleepData(
        date = this.date,
        sleepDuration = this.sleepDuration.toLong().minutes,
        sleepScore = this.sleepScore,
        bedTime = this.bedTime,
        wakeTime = this.wakeTime,
        deepSleepPercentage = this.deepSleepPercentage,
        remSleepPercentage = this.remSleepPercentage,
        lightSleepPercentage = this.lightSleepPercentage
    )
}
