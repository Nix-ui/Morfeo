package com.ucb.morfeo.features.sleepanalysis.domain.model

data class AudioAnalysis(
    val peaks: List<AudioPeak>,
    val snoreCount: Int,
    val movementCount: Int,
    val apneaEvents: Int
)
