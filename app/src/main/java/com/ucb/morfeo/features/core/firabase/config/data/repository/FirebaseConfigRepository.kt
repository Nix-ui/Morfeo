package com.ucb.morfeo.features.core.firabase.config.data.repository

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository
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
        remoteConfig.fetch(0)
        remoteConfig.activate().await()
        val maintenanceStatus = remoteConfig.getBoolean(MAINTENANCE_STATUS_KEY)
        return Result.success(maintenanceStatus)
    }
}