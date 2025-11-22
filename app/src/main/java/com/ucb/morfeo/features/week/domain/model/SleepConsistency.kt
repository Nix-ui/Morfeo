package com.ucb.morfeo.features.week.domain.model

import kotlin.time.Duration

enum class ConsistencyTrend {
    IMPROVING, DECLINING, STABLE
}

data class SleepConsistency(
    val consistencyScore: Float,
    val bedTimeVariance: Duration,
    val wakeTimeVariance: Duration,
    val sleepDurationVariance: Duration,
    val consistencyTrend: ConsistencyTrend
)