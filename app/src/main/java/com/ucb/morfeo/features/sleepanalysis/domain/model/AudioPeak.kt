package com.ucb.morfeo.features.sleepanalysis.domain.model

import com.ucb.morfeo.features.core.database.entity.PeakType

data class AudioPeak(
    val timestamp: Long,
    val amplitude: Float,
    val frequency: Float,
    val peakType: PeakType
)
