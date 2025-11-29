package com.ucb.morfeo.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ucb.morfeo.features.notification.data.worker.SleepNotificationWorker
import org.koin.core.component.get

import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class KoinWorkerFactory : WorkerFactory(), KoinComponent {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return when (workerClassName) {
            SleepNotificationWorker::class.java.name -> {
                get<SleepNotificationWorker>(
                    qualifier = null,
                    parameters = { parametersOf(appContext, workerParameters) }
                )
            }
            else -> throw IllegalArgumentException("No se encontró un worker definido para la clase: $workerClassName")
        }
    }
}
