package com.ucb.morfeo.features.week.domain.usecase

import com.ucb.morfeo.features.week.domain.model.ConsistencyTrend
import com.ucb.morfeo.features.week.domain.model.DailySleepData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.hours

class CalculateConsistencyUseCaseTest {

    private val useCase = CalculateConsistencyUseCase()

    @Test
    fun `when data is less than 2, return 100 score and stable trend`() {
        val dailyData = listOf(
            createDailyData(LocalDate(2025, 1, 1), 8.hours, 22, 0, 6, 0)
        )

        val result = useCase(dailyData)

        assertEquals(100f, result.consistencyScore)
        assertEquals(ConsistencyTrend.STABLE, result.consistencyTrend)
    }

    @Test
    fun `when data is perfectly consistent, return 100 score`() {
        val dailyData = listOf(
            createDailyData(LocalDate(2025, 1, 1), 8.hours, 22, 0, 6, 0),
            createDailyData(LocalDate(2025, 1, 2), 8.hours, 22, 0, 6, 0),
            createDailyData(LocalDate(2025, 1, 3), 8.hours, 22, 0, 6, 0)
        )

        val result = useCase(dailyData)

        assertEquals(100f, result.consistencyScore)
        assertEquals(ConsistencyTrend.STABLE, result.consistencyTrend)
    }

    @Test
    fun `when data has high variance, score should be lower than 100`() {
        val dailyData = listOf(
            createDailyData(LocalDate(2025, 1, 1), 8.hours, 22, 0, 6, 0),
            createDailyData(LocalDate(2025, 1, 2), 6.hours, 0, 0, 6, 0), // 2 horas de diferencia
            createDailyData(LocalDate(2025, 1, 3), 9.hours, 21, 0, 6, 0)
        )

        val result = useCase(dailyData)

        assertTrue("Score should be less than 100", result.consistencyScore < 100f)
    }

    private fun createDailyData(
        date: LocalDate,
        duration: kotlin.time.Duration,
        bedHour: Int,
        bedMinute: Int,
        wakeHour: Int,
        wakeMinute: Int
    ): DailySleepData {
        return DailySleepData(
            date = date,
            sleepDuration = duration,
            sleepScore = 80,
            bedTime = LocalDateTime(date.year, date.month, date.dayOfMonth, bedHour, bedMinute),
            wakeTime = LocalDateTime(date.year, date.month, date.dayOfMonth, wakeHour, wakeMinute),
            deepSleepPercentage = 0.2f,
            remSleepPercentage = 0.2f,
            lightSleepPercentage = 0.6f
        )
    }
}