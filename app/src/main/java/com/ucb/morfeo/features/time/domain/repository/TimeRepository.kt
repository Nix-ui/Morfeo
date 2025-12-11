package com.ucb.morfeo.features.time.domain.repository

interface TimeRepository {
    suspend fun syncAndGetOffset(): Long
}