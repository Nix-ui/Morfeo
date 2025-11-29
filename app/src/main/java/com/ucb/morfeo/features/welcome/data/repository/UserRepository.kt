package com.ucb.morfeo.features.welcome.data.repository

import com.ucb.morfeo.features.welcome.data.datasource.UserLocalDataSource
import com.ucb.morfeo.features.welcome.domain.model.UserModel
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class UserRepository(
    val localDataSource: UserLocalDataSource
): IWelcomeRepository {
    suspend fun insertUser(user: UserModel){
        localDataSource.insertUser(user)
    }
    override suspend fun getUser(): Flow<UserModel> {
        insertUser(UserModel("algo@example.com","password", burnDate = "2023-08-01",78.0,180.0))
        var user = localDataSource.getAll()
        return flow {
            emit(user[0])
        }
    }
}