package com.ucb.morfeo.features.sleepanalysis.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ucb.morfeo.features.sleepanalysis.database.entity.AudioAnalysis
import com.ucb.morfeo.features.sleepanalysis.database.entity.AudioPeak

@Dao
interface IAudioAnalysisDao {
    @Insert
    suspend fun insertAudioAnalysis(analysis: AudioAnalysis): Long
    @Update
    suspend fun updateAudioAnalysis(analysis: AudioAnalysis)
    @Query("SELECT * FROM audio_analysis WHERE sleepCoreId= :sleepCoreId")
    suspend fun getAnalysisForSleepCore(sleepCoreId:Int): AudioAnalysis?
    @Query("UPDATE audio_analysis SET analysisCompleted= :completed WHERE id= :analysisId")
    suspend fun updateAnalysisCompleted(analysisId: Int, completed: Boolean)
    @Query("DELETE FROM audio_analysis WHERE sleepCoreId= :sleepCoreId")
    suspend fun deleteAnalysis(sleepCoreId: Int)

    @Insert
    suspend fun insertAudioPeak(peak: AudioPeak)
    @Insert
    suspend fun insertAudioPeaks(peaks: List<AudioPeak>)
    @Query("SELECT * FROM audio_peak WHERE analysisId= :analysisId ORDER BY timestamp")
    suspend fun getPeaksForAnalysis(analysisId: Int): List<AudioPeak>
    @Query("DELETE FROM audio_peak WHERE analysisId= :analysisId")
    suspend fun deletePeaksForAnalysis(analysisId: Int)
    @Query("""
        SELECT COUNT(*)
        FROM audio_peak
        WHERE analysisId= :analysisId AND peakType= :peakType
    """)
    suspend fun countPeaksByType(analysisId: Int,peakType: AudioPeak.PeakType): Int
}