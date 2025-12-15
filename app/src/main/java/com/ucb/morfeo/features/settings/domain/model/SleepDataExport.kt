package com.ucb.morfeo.features.settings.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SleepDataExport(
    val date: String,
    val sleepDuration: Long? = null,
    val sleepScore: Int? = null
)
