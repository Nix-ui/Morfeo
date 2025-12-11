package com.ucb.morfeo.features.time.domain.usecase

import com.ucb.morfeo.features.time.domain.repository.TimeRepository

class GetRealTimeUseCase(private val repository: TimeRepository) {
    suspend operator fun invoke(): Long {
        return repository.syncAndGetOffset()
    }
}