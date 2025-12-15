package com.ucb.morfeo.features.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_peaks")
data class AudioPeak(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val analysisId: Int,
    val timestamp: Long,
    val amplitude: Float,
    val frequency: Float,
    val peakType: PeakType
)

enum class PeakType{
    SNORE, MOVEMENT, OTHER, APNEA
}