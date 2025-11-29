package com.ucb.morfeo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ucb.morfeo.di.KoinWorkerFactory
import com.ucb.morfeo.di.appModule
import com.ucb.morfeo.features.innernotification.data.NotificationManagerHelper
import com.ucb.morfeo.features.notification.data.worker.SleepNotificationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit
import org.koin.android.ext.android.get

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        createNotificationChannel()
        val periodicWork = PeriodicWorkRequestBuilder<SleepNotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "sleep_notification_worker",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWork
            )
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sleep_channel",
                "Recordatorios de sueño",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de hora de dormir"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}