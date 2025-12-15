package com.ucb.morfeo.features.sleepanalysis.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioFileManager(
    private val context: Context
) {
    fun createNewAudioFile(sleepCoreId: Int, timestamp: Long): File{
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "sleep_${sleepCoreId}_${dateFormat.format(Date(timestamp))}.m4a"
        return File(storageDir,fileName).apply {
            parentFile?.mkdirs()
        }
    }
    fun deleteAudioFile(file: File): Boolean{
        return try{
            if(file.exists()){
                file.delete()
            }else{
                true
            }
        }catch(e: Exception){
            false
        }
    }
    fun cleanupOldAudioFiles(maxAgeHours:Int = 2){
        try {
            val cutofftime = System.currentTimeMillis() - (maxAgeHours * 60 * 60 * 1000L )
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
            storageDir.listFiles()?.forEach {
                if(it.lastModified() < cutofftime && it.name.startsWith("sleep_")){
                    it.delete()
                }
            }
        }catch(e: Exception){

        }
    }
}