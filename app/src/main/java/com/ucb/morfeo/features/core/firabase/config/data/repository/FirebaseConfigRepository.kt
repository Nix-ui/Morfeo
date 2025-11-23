package com.ucb.morfeo.features.core.firabase.config.data.repository

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseConfigRepository: IFirebaseConfigRepository {
    companion object{
        const val MAINTENANCE_STATUS_KEY = "maintenance_status"
    }

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(remoteConfigSettings { minimumFetchIntervalInSeconds=3600 })
        fetchAndActivate()
    }
    override suspend fun getMaintenanceStatus(): Result<Boolean> {
        return try {
            val maintenanceStatus = remoteConfig.getBoolean(MAINTENANCE_STATUS_KEY)
            Result.success(maintenanceStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun listenToMaintenanceStatus(): Flow<Result<Boolean>> = callbackFlow {
        val listener = remoteConfig.addOnConfigUpdateListener(
            object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (configUpdate.updatedKeys.contains(MAINTENANCE_STATUS_KEY)) {
                        remoteConfig.activate().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newStatus = remoteConfig.getBoolean(MAINTENANCE_STATUS_KEY)
                                trySend(Result.success(newStatus))
                            } else {
                                trySend(Result.failure(task.exception ?: Exception("Error al activar Remote Config")))
                            }
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    trySend(Result.failure(error))
                }
            }
        )
        awaitClose { listener.remove() }
    }
}