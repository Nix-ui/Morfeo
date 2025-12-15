package com.ucb.morfeo.features.core.database.analyzer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import com.ucb.morfeo.features.core.database.entity.AudioPeak
import com.ucb.morfeo.features.core.database.entity.PeakType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AudioAnalyzer @Inject constructor(
    private val context: Context
) {
    suspend fun analyzeAudioFile(audioFile: File): List<AudioPeak> = withContext(Dispatchers.Default){
        val peaks = mutableListOf<AudioPeak>()
        try{
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(audioFile.absolutePath)
            }
            val chunkSize = 5000
            for (chunk in 0 until(getDuration(audioFile)/chunkSize)){
                val timestamp = chunk * chunkSize
                val amplitude = calculateAmplitude(audioFile,timestamp,chunkSize)
                val frequency = calculateFrequency(audioFile,timestamp,chunkSize)
                val peakType = detectPeakType(amplitude,frequency)
                if(amplitude > THRESHOLD){
                    peaks.add(
                        AudioPeak(
                            analysisId = 0,
                            timestamp = timestamp,
                            amplitude = amplitude,
                            frequency = frequency,
                            peakType = peakType
                        )
                    )
                }
            }
        }catch(e: Exception){
            Log.e("AudioAnalyzer","Error Analyzing audio file",e)
        }
        return@withContext peaks
    }
    private fun detectPeakType(amplitude: Float,frequency:Float): PeakType{
        return when{
            frequency in 40.0..300.0 && amplitude > 0.5 -> PeakType.SNORE
            amplitude > 0.7 -> PeakType.MOVEMENT
            frequency < 20.0 -> PeakType.APNEA
            else -> PeakType.OTHER
        }
    }
    private fun getDuration(audioFile:File): Long{
        return 28800000
    }
}