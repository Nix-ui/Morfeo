package com.ucb.morfeo.features.permissions.domain.usecase

import com.ucb.morfeo.features.permissions.domain.repository.PermissionRepository

class SetPermissionsGrantedUseCase(private val repository: PermissionRepository) {
    suspend operator fun invoke(granted: Boolean) = repository.setPermissionsGranted(granted)
}
