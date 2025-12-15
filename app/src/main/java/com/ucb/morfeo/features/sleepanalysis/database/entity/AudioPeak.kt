package com.ucb.morfeo.features.sleepanalysis.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_peak",
    foreignKeys = [
        ForeignKey(
            entity = AudioAnalysis::class,
            parentColumns = ["id"],
            childColumns = ["analysisId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AudioPeak(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val analysisId: Int,
    val timestamp: Long,
    val amplitude: Float,
    val frequency: Float,
    val peakType: PeakType
){
    enum class PeakType{
        SNORE, MOVEMENT, APNEA, OTHER
    }
}
