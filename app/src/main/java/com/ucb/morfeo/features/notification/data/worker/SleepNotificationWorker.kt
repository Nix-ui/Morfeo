package com.ucb.morfeo.features.notification.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ucb.morfeo.features.innernotification.data.NotificationManagerHelper
import com.ucb.morfeo.features.innernotification.data.datasource.LocalNotificationDataSource
import com.ucb.morfeo.features.innernotification.data.datastore.local.LocalNotificationDataStore
import com.ucb.morfeo.features.innernotification.data.repository.NotificationRepository
import com.ucb.morfeo.features.innernotification.domain.model.AppNotification
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.usecases.RecivedNotificationUseCase
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import com.ucb.morfeo.features.welcome.data.database.AppRoomDatabase
import kotlinx.coroutines.flow.first
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent
import java.time.LocalTime
import kotlin.random.Random


class SleepNotificationWorker(
    private val appContext: Context,
    workParams: WorkerParameters,
): CoroutineWorker(appContext,workParams), KoinComponent{
    private val settingsDataStore: SettingsDataStore by inject()
    private val recivedNotificationUseCase: RecivedNotificationUseCase by inject()
    private val notificationManagerHelper = NotificationManagerHelper(appContext)

    override suspend fun doWork(): Result {
        val (sleepHour, sleepMinute) = settingsDataStore.getSleepTime().first()
        val (wakeHour, wakeMinute) = settingsDataStore.getWakeupTime().first()
        val now = LocalTime.now()

        Log.d("SleepWorker", "Hora actual: $now. Configuración -> Dormir: $sleepHour:$sleepMinute, Despertar: $wakeHour:$wakeMinute")

        val wakeUpTime = LocalTime.of(wakeHour, wakeMinute)
        if (now.isAfter(wakeUpTime) && now.isBefore(wakeUpTime.plusMinutes(30))) {
            Log.d("SleepWorker", "CONDICIÓN DE DESPERTAR CUMPLIDA. Enviando notificación.")
            sendNotification(
                NotificationModel(
                    id = 0,
                    title = "Hora de Despertar",
                    content = "Es momento de empezar el dia.",
                    timeStampRecived = System.currentTimeMillis(),
                    path = "/home",
                    timeStampReaded = null
                )
            )
        }

        val sleepTime = LocalTime.of(sleepHour, sleepMinute)
        if (now.isAfter(sleepTime) && now.isBefore(sleepTime.plusMinutes(30))) {
            Log.d("SleepWorker", "CONDICIÓN DE DORMIR CUMPLIDA. Enviando notificación.")
            sendNotification(
                NotificationModel(
                    id = 0,
                    title = "Hora de dormir",
                    content = "Es momento de ir a la cama.",
                    timeStampRecived = System.currentTimeMillis(),
                    timeStampReaded = null,
                    path = "/home"
                )
            )
        }
        Log.d("SleepNotificationWorker", "doWork: ${now.hour}:${now.minute}")
        return Result.success()
    }
    private suspend fun sendNotification(notification: NotificationModel){
        notificationManagerHelper.showSleepNotification(
            title= notification.title,
            content = notification.content
        )
        recivedNotificationUseCase.invoke(notification)
    }
}