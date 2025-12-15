package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ucb.morfeo.features.core.database.entity.AudioAnalysis
import com.ucb.morfeo.features.core.database.entity.AudioPeak

@Dao
interface AudioAnalysisDao {
    @Insert
    suspend fun insertAudioAnalysis(analysis: AudioAnalysis): Long
    @Insert
    suspend fun insertAudioPeak(peak: AudioPeak)
    @Insert
    suspend fun insertAudioPeaks(peaks: List<AudioPeak>)
    @Query("SELECT * FROM audio_analysis WHERE sleepCoreId= :sleepCoreId")
    suspend fun getAnalysisForSleepCore(sleepCoreId: Int): AudioAnalysis?
    @Query("SELECT * FROM audio_peaks WHERE analysisId= :analysisId ORDER BY timestamp")
    suspend fun getPeaaksForAnalysis(analysisId: Int): List<AudioPeak>
    @Query("UPDATE audio_analysis SET analysisCompleted=:analysisCompleted WHERE id=:analysisId")
    suspend fun updateAnalysisCompled(analysisId: Int, analysisCompleted: Boolean)
    @Query("DELETE FROM audio_analysis WHERE sleepCoreid= :sleepCoreId")
    suspend fun deleteAnalysis(sleepCoreId: Int)
}