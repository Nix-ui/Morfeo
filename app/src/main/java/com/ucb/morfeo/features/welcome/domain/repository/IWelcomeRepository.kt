package com.ucb.morfeo.features.welcome.domain.repository

import com.ucb.morfeo.features.welcome.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface IWelcomeRepository {
    suspend fun getUser(): Flow<UserModel>
}