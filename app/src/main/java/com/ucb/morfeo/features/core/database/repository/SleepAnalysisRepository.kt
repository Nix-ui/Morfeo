package com.ucb.morfeo.features.core.database.repository

import android.util.Log
import com.ucb.morfeo.features.core.database.analyzer.AudioAnalyzer
import com.ucb.morfeo.features.core.database.dao.AudioAnalysisDao
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.AudioAnalysis
import com.ucb.morfeo.features.core.database.entity.AudioPeak
import com.ucb.morfeo.features.core.database.entity.PeakType
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.io.File
import javax.inject.Inject

class SleepAnalysisRepository @Inject constructor(
    private val sleepDao: SleepDao,
    private val audioAnalysisDao: AudioAnalysisDao,
    private val audioAnalyzer: AudioAnalyzer,
    private val fileManager: AudioFileManager
) {
    suspend fun startSleepTracking(
        userEmail: String,
        bedTime: LocalDateTime,
        scheduleWakeTime: LocalDateTime
    ): Result<Int>{
        return try {
            val sleepCore = SleepCore(
                userEmail = userEmail,
                date = bedTime.date,
                sleepDuration = 0L,
                sleepScore = 0,
                bedTime = bedTime,
                wakeTime = scheduleWakeTime,
                deepSleepPercentage = 0f,
                remSleepPercentage = 0f,
                lightSleepPercentage = 0f,
                awakeDuration = 0L
            )
            val sleepCoreId = sleepDao.insertSleepCore(sleepCore).toInt()
            Result.success(sleepCoreId)
        }catch(e: Exception){
            Result.failure(e)
        }
    }

    suspend fun completeSleepAnalysis(
        sleepCoreId: Int,
        audioFile: File
    ): Result<Unit>{
        return withContext(Dispatchers.IO){
            try{
                val peaks = audioAnalyzer.analyzeAudioFile(audioFile)
                val analysis = AudioAnalysis(
                    sleepCoreId = sleepCoreId,
                    audioFilePath = audioFile.absolutePath,
                    totalPeaks = peaks.size,
                    snoreCount = peaks.count{ it.peakType == PeakType.SNORE},
                    movementCount = peaks.count{ it.peakType == PeakType.MOVEMENT},
                )
                val analysisId = audioAnalysisDao.insertAudioAnalysis(analysis).toInt()
                val peaksWithAnalysisId = peaks.map { peak ->
                    peak.copy(analysisId=analysisId)
                }
                audioAnalysisDao.insertAudioPeaks(peaksWithAnalysisId)

                val sleepMteics = calculateSleepMetrics(peaks)
                val slepCore = sleepDao.getSleepCoreById(sleepCoreId = sleepCoreId)
                fileManager.deleteAudioFile(audioFile)
                audioAnalysisDao.updateAnalysisCompled(analysisId,true)
                Result.success(Unit)
            }catch(e: Exception){
                Log.e("SleepAnalysisRepository","Analysis Failed",e)
                Result.failure(e)
            }
        }
    }
    private fun calculateSleepMetrics(peaks: List<AudioPeak>): SleepMetrics{
        val totalDuration = peaks.lastOrNull()?.timestamp ?: 0L
        return SleepMetrics(
            deepSleepPercentage = calculateDeepSleepPercentage(peaks),
            remSleepPercentage = calculateRemSleepPercentage(peaks),
            lightSleepPercentage = calculateLightSleepPercentage(peaks),
            awakeDuration = calculateAwakeTime(peaks),
            sleepScore = calculateSleepScore(peaks)
        )
    }
}

data class SleepMetrics(
    val deepSleepPercentage: Float,
    val remSleepPercentage: Float,
    val lightSleepPercentage: Float,
    val awakeDuration: Long,
    val sleepScore: Int
)