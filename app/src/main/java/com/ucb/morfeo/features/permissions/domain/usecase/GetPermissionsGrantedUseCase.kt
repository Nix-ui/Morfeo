package com.ucb.morfeo.features.permissions.domain.usecase

import com.ucb.morfeo.features.permissions.domain.repository.PermissionRepository

class GetPermissionsGrantedUseCase(private val repository: PermissionRepository) {
    operator fun invoke() = repository.permissionsGranted
}
