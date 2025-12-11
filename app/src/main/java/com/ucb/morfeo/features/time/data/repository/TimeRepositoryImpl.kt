package com.ucb.morfeo.features.time.data.repository

import android.os.SystemClock
import com.ucb.morfeo.features.time.data.local.TimeLocalDataSource
import com.ucb.morfeo.features.time.data.remote.TimeApi
import com.ucb.morfeo.features.time.domain.repository.TimeRepository
import java.util.concurrent.TimeUnit

class TimeRepositoryImpl(
    private val api: TimeApi,
    private val localDataSource: TimeLocalDataSource
) : TimeRepository {

    override suspend fun syncAndGetOffset(): Long {
        return try {
            val response = api.getCurrentTime()
            val serverTimeMillis = TimeUnit.SECONDS.toMillis(response.unixtime)
            val currentElapsed = SystemClock.elapsedRealtime()

            localDataSource.saveTimeSnapshot(serverTimeMillis, currentElapsed)
            serverTimeMillis - currentElapsed

        } catch (e: Exception) {
            val snapshot = localDataSource.getTimeSnapshot()
                ?: throw Exception("Se requiere conexión a internet para la sincronización inicial.")

            val (savedServerTime, savedElapsed) = snapshot
            val currentElapsed = SystemClock.elapsedRealtime()
            if (currentElapsed < savedElapsed) {
                throw Exception("Dispositivo reiniciado. Conéctate a internet para resincronizar.")
            }
            savedServerTime - savedElapsed
        }
    }
}