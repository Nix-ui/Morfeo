package com.ucb.morfeo.features.welcome.domain.usecase

import com.ucb.morfeo.features.welcome.domain.model.UserModel
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import kotlinx.coroutines.flow.Flow

class FetchUserCase(
    val repository: IWelcomeRepository
) {
    suspend fun invoke(): Flow<UserModel>{
        return repository.getUser()
    }
}