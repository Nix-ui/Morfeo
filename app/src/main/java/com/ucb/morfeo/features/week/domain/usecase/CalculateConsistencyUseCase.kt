package com.ucb.morfeo.features.week.domain.usecase

import com.ucb.morfeo.features.week.domain.model.ConsistencyTrend
import com.ucb.morfeo.features.week.domain.model.DailySleepData
import com.ucb.morfeo.features.week.domain.model.SleepConsistency
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CalculateConsistencyUseCase {
    operator fun invoke(dailyData: List<DailySleepData>): SleepConsistency {
        if (dailyData.size < 2) {
            return SleepConsistency(
                consistencyScore = 100f,
                bedTimeVariance = Duration.ZERO,
                wakeTimeVariance = Duration.ZERO,
                sleepDurationVariance = Duration.ZERO,
                consistencyTrend = ConsistencyTrend.STABLE
            )
        }

        val bedTimeVariance = calculateTimeVariance(dailyData.map { it.bedTime })
        val wakeTimeVariance = calculateTimeVariance(dailyData.map { it.wakeTime })
        val sleepDurationVariance = calculateDurationVariance(dailyData.map { it.sleepDuration})

        val consistencyScore = calculateConsistencyScore(
            bedTimeVariance,
            wakeTimeVariance,
            sleepDurationVariance
        )

        return SleepConsistency(
            consistencyScore = consistencyScore,
            bedTimeVariance = bedTimeVariance,
            wakeTimeVariance = wakeTimeVariance,
            sleepDurationVariance = sleepDurationVariance,
            consistencyTrend = determineTrend(dailyData)
        )
    }

    private fun calculateTimeVariance(dateTimes: List<kotlinx.datetime.LocalDateTime>): Duration {
        val secondsList = dateTimes.map { datetime ->
            datetime.hour * 3600L + datetime.minute * 60L + datetime.second
        }
        val averageSeconds = secondsList.average().toLong()

        val variances = secondsList.map { seconds ->
            kotlin.math.abs(seconds - averageSeconds)
        }

        val averageVariance = variances.average().toLong()
        return averageVariance.seconds
    }

    private fun calculateDurationVariance(durations: List<Duration>): Duration {
        if (durations.isEmpty()) return Duration.ZERO

        val averageSeconds = durations.map { it.inWholeSeconds.toDouble() }.average().toLong()
        val varianceSeconds = durations.map { duration ->
            kotlin.math.abs(duration.inWholeSeconds - averageSeconds)
        }.average().toLong()

        return varianceSeconds.seconds
    }

    private fun calculateConsistencyScore(
        bedTimeVariance: Duration,
        wakeTimeVariance: Duration,
        sleepDurationVariance: Duration
    ): Float {
        val bedTimeWeight = 0.4f
        val wakeTimeWeight = 0.4f
        val durationWeight = 0.2f
        val maxVariance = 3.hours

        val bedTimeScore = 100 - (bedTimeVariance.inWholeSeconds.toFloat() / maxVariance.inWholeSeconds * 100).coerceIn(0f, 100f)
        val wakeTimeScore = 100 - (wakeTimeVariance.inWholeSeconds.toFloat() / maxVariance.inWholeSeconds * 100).coerceIn(0f, 100f)
        val durationScore = 100 - (sleepDurationVariance.inWholeSeconds.toFloat() / maxVariance.inWholeSeconds * 100).coerceIn(0f, 100f)

        return (bedTimeScore * bedTimeWeight + wakeTimeScore * wakeTimeWeight + durationScore * durationWeight)
    }

    private fun determineTrend(dailyData: List<DailySleepData>): ConsistencyTrend {
        if (dailyData.size < 3) return ConsistencyTrend.STABLE

        val firstHalf = dailyData.take(dailyData.size / 2)
        val secondHalf = dailyData.takeLast(dailyData.size / 2)

        val firstScore = calculateConsistencyScore(
            calculateTimeVariance(firstHalf.map { it.bedTime }),
            calculateTimeVariance(firstHalf.map { it.wakeTime }),
            calculateDurationVariance(firstHalf.map { it.sleepDuration})
        )

        val secondScore = calculateConsistencyScore(
            calculateTimeVariance(secondHalf.map { it.bedTime }),
            calculateTimeVariance(secondHalf.map { it.wakeTime }),
            calculateDurationVariance(secondHalf.map { it.sleepDuration})
        )

        return when {
            secondScore > firstScore + 5 -> ConsistencyTrend.IMPROVING
            secondScore < firstScore - 5 -> ConsistencyTrend.DECLINING
            else -> ConsistencyTrend.STABLE
        }
    }
}