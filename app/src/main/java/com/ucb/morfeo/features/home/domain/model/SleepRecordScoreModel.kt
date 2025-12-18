package com.ucb.morfeo.features.home.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class SleepRecordScoreModel(
    val date: LocalDate,
    val avgSleepDuration: Long,
    val wakeTime: LocalDateTime,
    val bedTime: LocalDateTime,
    val sleepScore: Int
)