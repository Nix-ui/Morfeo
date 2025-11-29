package com.ucb.morfeo.features.login.domain.repository

import com.ucb.morfeo.features.login.domain.model.UserModel

interface ICheckSessionRepository {
    suspend fun checkActiveSession(): Result<UserModel>
}