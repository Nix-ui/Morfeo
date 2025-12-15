package com.ucb.morfeo.features.sleepanalysis.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ucb.morfeo.features.core.database.entity.SleepCore

@Entity(
    tableName = "audio_analysis",
    foreignKeys = [
        ForeignKey(
            entity = SleepCore::class,
            parentColumns = ["id"],
            childColumns = ["sleepCoreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AudioAnalysis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sleepCoreId: Int,
    val audioFilePath: String,
    val analysisCompleted: Boolean = false,
    val totalPeaks: Int = 0,
    val snoreCount: Int = 0,
    val movementCount: Int=0,
    val apneaEvents: Int = 0
)
