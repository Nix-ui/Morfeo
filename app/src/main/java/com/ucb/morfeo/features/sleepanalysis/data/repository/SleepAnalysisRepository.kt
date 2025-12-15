package com.ucb.morfeo.features.sleepanalysis.data.repository

import android.content.Context
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.sleepanalysis.database.dao.IAudioAnalysisDao
import com.ucb.morfeo.features.sleepanalysis.database.entity.AudioAnalysis
import com.ucb.morfeo.features.sleepanalysis.database.entity.AudioPeak
import com.ucb.morfeo.features.sleepanalysis.domain.model.*
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository
import com.ucb.morfeo.features.sleepanalysis.domain.repository.ISleepAnalysisRepository.SleepStatistics
import com.ucb.morfeo.features.sleepanalysis.utils.AudioAnalyzer
import com.ucb.morfeo.features.sleepanalysis.utils.AudioAnalyzer.SleepMetrics
import com.ucb.morfeo.features.sleepanalysis.utils.AudioFileManager
import com.ucb.morfeo.features.sleepanalysis.utils.AudioRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.io.File

class SleepAnalysisRepository(
    private val sleepDao: SleepDao,
    private val audioAnalysisDao: IAudioAnalysisDao,
    private val audioRecorder: AudioRecorder,
    private val audioAnalyzer: AudioAnalyzer,
    private val audioFileManager: AudioFileManager,
    private val context: Context
): ISleepAnalysisRepository {
    override suspend fun startSleepSession(
        userEmail: String,
        bedTime: LocalDateTime,
        scheduledWakeTime: LocalDateTime
    ): Result<SleepSession> {
        return try{
            val sleepCore = SleepCore(
                userEmail= userEmail,
                date= bedTime.date,
                sleepDuration = 0L,
                sleepScore = 0,
                bedTime= bedTime,
                wakeTime = scheduledWakeTime,
                deepSleepPercentage = 0f,
                remSleepPercentage = 0f,
                lightSleepPercentage = 0f,
                awakeDuration = 0L
            )
            val sleepCoreId = sleepDao.insertSleepCore(sleepCore).toInt()
            val timestamp = System.currentTimeMillis()
            val audioFile = audioFileManager.createNewAudioFile(sleepCoreId, timestamp)
            val recordingStarted = audioRecorder.startRecording(audioFile.absolutePath)
            if(!recordingStarted){
                throw Exception("Failed to start recording")
            }
            Result.success(
                SleepSession(
                    id = sleepCoreId,
                    userEmail = userEmail,
                    bedTime = bedTime,
                    scheduledWakeTime = scheduledWakeTime,
                    audioFilePath = audioFile.absolutePath,
                    startTimeStamp = timestamp
                )
            )
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun stopSleepSession(
        sessionId: Int,
        wakeTime: LocalDateTime
    ): Result<SleepAnalysis> {
        return try{
            val audioFile = audioRecorder.stopRecording()
            val analysisResult = audioAnalyzer.analyzeaudioFile(audioFile)
            val sleepMetrics = calculateSleepMetrics(analysisResult)
            val audioAnalysisId = saveAudioAnalysis(sessionId,analysisResult,audioFile.absolutePath)
            saveAudioPeaks(audioAnalysisId, analysisResult.peaks)
            updateSleepCoreWithMetrics(sessionId,wakeTime,sleepMetrics)
            audioFileManager.deleteAudioFile(audioFile)
            val sleepCore = sleepDao.getSleepCoreById(sessionId)
                ?: throw Exception("SleepCore not found")
            Result.success(mapToDomainSleepAnalysis(sleepCore,sleepMetrics,analysisResult))
        }catch(e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun analyzeSleepAudio(
        audioFilePath: String,
        sessionId: Int
    ): Result<SleepAnalysis> {
        return try{
            val audioFile = File(audioFilePath)
            if(!audioFile.exists()){
                return Result.failure((Exception("Audio file not found")))
            }
            val currentMoment: Instant = Clock.System.now()
            val analysisResult = audioAnalyzer.analyzeaudioFile(audioFile)
            val sleepMetrics = calculateSleepMetrics(analysisResult)
            val analysisId = saveAudioAnalysis(sessionId,analysisResult,audioFilePath)
            saveAudioPeaks(sessionId, analysisResult.peaks)
            updateSleepCoreWithMetrics(sessionId, currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()),sleepMetrics)
            audioFileManager.deleteAudioFile(audioFile)
            val sleepCore = sleepDao.getSleepCoreById(sessionId) ?: return Result.failure(Exception("SleepCore not found"))
            Result.success(mapToDomainSleepAnalysis(sleepCore, sleepMetrics, analysisResult))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getSleepHistory(userEmail: String): Flow<List<SleepAnalysis>> {
        return sleepDao.getSleepHistory(userEmail).map { sleepCores ->
            sleepCores.map {
                mapToDomainSleepAnalysis(it,null,null)
            }
        }
    }

    override suspend fun cancelSleepSession(sessionId: Int): Result<Unit> {
        return try{
            audioRecorder.stopRecording()
            sleepDao.deleteSleepCore(sessionId)
            audioAnalysisDao.deleteAnalysis(sessionId)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getSleepStatistics(userEmail: String): ISleepAnalysisRepository.SleepStatistics? {
        return sleepDao.getSleepStatistics(userEmail)?.let { stats->
            SleepStatistics(
                avgScore = stats.avgScore,
                avgDeepSleep = stats.avgDeepSleep,
                avgDuration = stats.avgDuration,
                totalNights = stats.totalNights
            )
        }
    }
    private fun calculateSleepMetrics(analysisResult: AudioAnalyzer.AudioAnalysisResult): SleepMetrics{
        return analysisResult.sleepMetrics
    }
    private suspend fun saveAudioAnalysis(sleepCoreId:Int, analysisResult: AudioAnalyzer.AudioAnalysisResult,audioFilePath: String): Int{
        val audioAnalysis = AudioAnalysis(
            sleepCoreId = sleepCoreId,
            audioFilePath = audioFilePath,
            analysisCompleted = true,
            totalPeaks = analysisResult.peaks.size,
            snoreCount = analysisResult.snoreCount,
            movementCount = analysisResult.movementCount,
            apneaEvents = analysisResult.apneaEvents
        )
        return audioAnalysisDao.insertAudioAnalysis(audioAnalysis).toInt()
    }
    private suspend fun saveAudioPeaks(analysisId: Int,peaks: List<AudioAnalyzer.AudioPeak>){
        val audioPeaks = peaks.map { peak->
            AudioPeak(
                analysisId = analysisId,
                timestamp = peak.timestamp,
                amplitude = peak.amplitude,
                frequency = peak.frequency,
                peakType = when(peak.peakType){
                    AudioAnalyzer.PeakType.SNORE -> AudioPeak.PeakType.SNORE
                    AudioAnalyzer.PeakType.MOVEMENT -> AudioPeak.PeakType.MOVEMENT
                    AudioAnalyzer.PeakType.APNEA -> AudioPeak.PeakType.APNEA
                    else -> AudioPeak.PeakType.OTHER
                }
            )
        }
        audioAnalysisDao.insertAudioPeaks(audioPeaks)
    }
    private suspend fun  updateSleepCoreWithMetrics(sessionId:Int, wakeTime: LocalDateTime, metrics: SleepMetrics){
        val sleepCore = sleepDao.getSleepCoreById(sessionId)?: return
        val updateSleepCore = sleepCore.copy(
            wakeTime = wakeTime,
            sleepDuration = calculateSleepDuration(sleepCore.bedTime,wakeTime),
            sleepScore = metrics.sleepScore,
            deepSleepPercentage = metrics.deepSleepPercentage,
            remSleepPercentage = metrics.remSleepPercentage,
            lightSleepPercentage = metrics.lightSleepPercentage,
            awakeDuration = metrics.awakeDuration
        )
        sleepDao.updateSleepCore(updateSleepCore)
    }
    private fun calculateSleepDuration(bedTime: LocalDateTime, wakeTime: LocalDateTime): Long{
        val bedMillis = bedTime.toJavaLocalDateTime()
            .toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli()
        val wakeMillis = wakeTime.toJavaLocalDateTime()
            .toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli()
        return wakeMillis - bedMillis
    }
    private fun mapToDomainSleepAnalysis(
        sleepCore:SleepCore,
        sleepMetrics: SleepMetrics?,
        audioAnalysisResult: AudioAnalyzer.AudioAnalysisResult?
    ): SleepAnalysis{
        return SleepAnalysis(
            id = sleepCore.id,
            userEmail = sleepCore.userEmail,
            date = sleepCore.date.toString(),
            sleepDuration = sleepCore.sleepDuration,
            sleepScore = sleepMetrics?.sleepScore ?: sleepCore.sleepScore,
            bedTime = sleepCore.bedTime,
            wakeTime = sleepCore.wakeTime,
            deepSleepPercentage = sleepMetrics?.deepSleepPercentage ?: sleepCore.deepSleepPercentage,
            remSleepPercentage = sleepMetrics?.remSleepPercentage ?: sleepCore.remSleepPercentage,
            lightSleepPercentage = sleepMetrics?.lightSleepPercentage ?: sleepCore.lightSleepPercentage,
            awakeDuration = sleepMetrics?.awakeDuration ?: sleepCore.awakeDuration,
            audioAnalysis = audioAnalysisResult?.let {
                DomainAudioAnalysis(
                    peaks = it.peaks.map { peak ->
                        DomainAudioPeak(
                            timestamp = peak.timestamp,
                            amplitude = peak.amplitude,
                            frequency = peak.frequency,
                            peakType = when(peak.peakType){
                                AudioAnalyzer.PeakType.SNORE -> PeakType.SNORE
                                AudioAnalyzer.PeakType.MOVEMENT -> PeakType.MOVEMENT
                                AudioAnalyzer.PeakType.APNEA -> PeakType.APNEA
                                else -> PeakType.OTHER
                            }
                        )
                    },
                    snoreCount = it.snoreCount,
                    movementCount = it.movementCount,
                    apneaEvents = it.apneaEvents
                )
            }
        )
    }
}