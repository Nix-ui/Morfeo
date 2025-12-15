package com.ucb.morfeo.features.sleepanalysis.domain.model


data class AudioPeak(
    val timestamp: Long,
    val amplitude: Float,
    val frequency: Float,
    val peakType: PeakType
)
