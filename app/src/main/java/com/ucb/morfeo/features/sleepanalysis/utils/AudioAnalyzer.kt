package com.ucb.morfeo.features.sleepanalysis.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File
import java.time.Duration
import kotlin.math.PI
import kotlin.math.sin

class AudioAnalyzer(
    private val context: Context
) {
    data class AudioAnalysisResult(
        val peaks: List<AudioPeak>,
        val sleepMetrics: SleepMetrics,
        val snoreCount: Int,
        val movementCount: Int,
        val apneaEvents: Int
    )
    data class AudioPeak(
        val timestamp:Long,
        val amplitude: Float,
        val frequency: Float,
        val peakType: PeakType
    )
    data class SleepMetrics(
        val deepSleepPercentage: Float,
        val remSleepPercentage: Float,
        val lightSleepPercentage: Float,
        val awakeDuration: Long,
        val sleepScore: Int
    )
    enum class PeakType{
        SNORE, MOVEMENT, APNEA, OTHER
    }
    companion object{
        private const val TAG= "AudioAnalyzer"
        private const val CHUNK_SIZE_MS= 5000L
        private const val AMPLITUDE_THRESHOLD= 0.3f
        private const val SNORE_FREQ_MIN= 40f
        private const val SNORE_FREQ_MAX= 300f
        private const val MOVEMENT_AMPLITUDE_THRESHOLD = 0.7f
        private const val APNEA_SILENCE_DURATION= 10000L
    }
    fun analyzeaudioFile(audioFile: File): AudioAnalysisResult{
        return try{
            val duration = getAudioDuration(audioFile)
            Log.d(TAG, "Analyzing audio file: ${audioFile.name},duration: ${duration}ms")
            val peaks = analyzeAudioChunks(duration)
            val sleepMetrics = calculateSleepMetrics(peaks,duration)

            val snoreCount = peaks.count { it.peakType == PeakType.SNORE }
            val movementCount = peaks.count { it.peakType == PeakType.MOVEMENT }
            val apneaEvents = detectApneaEvents(peaks)
            AudioAnalysisResult(
                peaks = peaks,
                sleepMetrics = sleepMetrics,
                snoreCount = snoreCount,
                movementCount = movementCount,
                apneaEvents = apneaEvents
            )
        }catch(e: Exception){
            Log.e(TAG,"Error Analyzing audio file",e)
            AudioAnalysisResult(
                peaks = emptyList(),
                sleepMetrics = SleepMetrics(0f,0f,0f,0L,0),
                snoreCount = 0,
                movementCount = 0,
                apneaEvents = 0
            )
        }
    }
    private fun getAudioDuration(audioFile: File): Long{
        return  try{
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLong() ?: 0L
        }catch(e: Exception){
            Log.e(TAG,"Error getting audio duration",e)
            0L
        }
    }
    private fun analyzeAudioChunks(duration: Long): List<AudioPeak>{
        val peaks = mutableListOf<AudioPeak>()
        var currentTime = 0L
        while(currentTime < duration){
            val chunkEnd = minOf(currentTime + CHUNK_SIZE_MS,duration)
            val amplitude = calculateAmplitude(currentTime,duration)
            val frequency = calculateFrequency(currentTime, duration)
            if(amplitude > AMPLITUDE_THRESHOLD){
                val peakType = determinatePeakType(amplitude,frequency)
                peaks.add(
                    AudioPeak(
                        timestamp = currentTime,
                        amplitude = amplitude,
                        frequency = frequency,
                        peakType =  peakType
                    )
                )
            }
            currentTime= chunkEnd
        }
        return peaks
    }
    private fun calculateAmplitude(timestamp: Long, totalDuration: Long): Float{
        val timeInHours = timestamp / (60.0*60.0*1000.0)
        val baseAmplitude = 0.3f + 0.3f * sin(timeInHours * 2* PI).toFloat()
        val noise = (Math.random() * 0.2).toFloat()
        return (baseAmplitude + noise).coerceIn(0f,1f)
    }
    private fun calculateFrequency(timestamp: Long, totalDuration: Long): Float{
        val progress = timestamp.toFloat() / totalDuration.toFloat()
        return when{
            progress < 0.3 -> 2f + (Math.random() * 2).toFloat()
            progress < 0.6 -> 6f + (Math.random() * 6).toFloat()
            else -> 15f + (Math.random() * 15).toFloat()
        }
    }
    private fun determinatePeakType(amplitude: Float, frequency: Float): PeakType{
        return when{
            frequency in SNORE_FREQ_MIN..SNORE_FREQ_MAX && amplitude > 0.4f -> PeakType.SNORE
            amplitude > MOVEMENT_AMPLITUDE_THRESHOLD -> PeakType.MOVEMENT
            amplitude < 0.1f ->PeakType.APNEA
            else -> PeakType.OTHER
        }
    }
    private fun calculateSleepMetrics(peaks: List<AudioPeak>,totalDuration: Long): SleepMetrics{
        if(peaks.isEmpty()){
            return SleepMetrics(0f,0f,0f,0L,0)
        }
        val deepSleepPercentage = calculateDeepSleepPercentage(peaks)
        val remSleepPercentage = calculateRemSleepPercentage(peaks)
        val lightSleepPercentage = calculateLightSleepPercentage(peaks)
        val awakeDuration = calculateAwakeTime(peaks,totalDuration)
        val sleepScore = calculateSleepScore(peaks,deepSleepPercentage,remSleepPercentage,awakeDuration,totalDuration)
        return SleepMetrics(
            deepSleepPercentage = deepSleepPercentage,
            remSleepPercentage = remSleepPercentage,
            lightSleepPercentage = lightSleepPercentage,
            awakeDuration = awakeDuration,
            sleepScore = sleepScore
        )
    }
    private fun calculateDeepSleepPercentage(peaks: List<AudioPeak>): Float{
        val deepSleepPeaks = peaks.count{ it.frequency < 4f && it.amplitude < 0.4f}
        return (deepSleepPeaks.toFloat() / peaks.size)* 100f
    }
    private fun calculateRemSleepPercentage(peaks: List<AudioPeak>): Float{
        val remSleepPeaks = peaks.count{it.frequency in 4f..13f && it.peakType==PeakType.SNORE}
        return (remSleepPeaks.toFloat() / peaks.size) * 100f
    }
    private fun calculateLightSleepPercentage(peaks: List<AudioPeak>): Float{
        val lightSleepPeaks = peaks.count{it.frequency in 4f..8f && it.amplitude in 0.3f..0.6f}
        return (lightSleepPeaks.toFloat() / peaks.size) * 100f
    }
    private fun calculateAwakeTime(peaks: List<AudioPeak>,totalDuration: Long): Long{
        val awakeChunks = peaks.count{ it.amplitude > 0.7f || it.frequency > 13f}
        val chunkSize = CHUNK_SIZE_MS
        return (awakeChunks*chunkSize).coerceAtMost(totalDuration)
    }
    private fun calculateSleepScore(
        peaks: List<AudioPeak>,
        deepSleep: Float,
        remSleep: Float,
        awakeDuration: Long,
        totalDuration: Long
    ): Int{
        val deepSleepScore = (deepSleep/ 25f) * 40f
        val remSleepScore = (remSleep/25f) * 30f
        val awakePenality = (awakeDuration.toFloat() / totalDuration.toFloat()) * 30f
        val efficiencyScore = 30f - awakePenality
        val totalScore= deepSleepScore + remSleepScore + efficiencyScore
        return totalScore.coerceIn(0f,100f).toInt()
    }
    private fun detectApneaEvents(peaks: List<AudioPeak>): Int{
        var apneaEvents= 0
        var currentSilenceStart: Long? =null
        for (peak in peaks.sortedBy { it.timestamp }){
            if(peak.peakType == PeakType.APNEA){
                if(currentSilenceStart == null){
                    currentSilenceStart= peak.timestamp
                }
                if(currentSilenceStart != null &&
                    peak.timestamp - currentSilenceStart >= APNEA_SILENCE_DURATION){
                    apneaEvents++
                    currentSilenceStart= null
                }
            }else{
                currentSilenceStart = null
            }
        }
        return apneaEvents
    }
}