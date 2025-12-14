package com.ucb.morfeo.features.permissions.domain.repository

import kotlinx.coroutines.flow.Flow

interface PermissionRepository {
    val permissionsGranted: Flow<Boolean>
    suspend fun setPermissionsGranted(granted: Boolean)
}
