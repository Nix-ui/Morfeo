package com.ucb.morfeo.features.sleepanalysis.utils

import android.media.MediaRecorder
import java.io.File

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var isRecording = false

    fun startRecording(filePath:String): Boolean{
        return try{
            mediaRecorder?.stop()
            mediaRecorder?.release()

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioChannels(1)
                setAudioEncodingBitRate(128000)
                setOutputFile(filePath)
                prepare()
                start()
            }

            currentFilePath= filePath
            isRecording= true
            true
        }catch(e: Exception){
            e.printStackTrace()
            false
        }
    }
    fun stopRecording(): File{
        try{
            mediaRecorder?.apply {
                stop()
                release()
            }
        }catch(e: Exception){
            e.printStackTrace()
        }
        mediaRecorder = null
        isRecording=false
        val file = File(currentFilePath ?: "")
        currentFilePath = null
        return file
    }
    fun isRecording(): Boolean = isRecording
}