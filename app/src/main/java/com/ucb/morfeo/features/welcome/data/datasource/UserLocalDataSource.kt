package com.ucb.morfeo.features.welcome.data.datasource

import com.ucb.morfeo.features.welcome.data.database.dao.IUserDao
import com.ucb.morfeo.features.welcome.data.mapper.toEntity
import com.ucb.morfeo.features.welcome.data.mapper.toModel
import com.ucb.morfeo.features.welcome.domain.model.UserModel

class UserLocalDataSource(
    val dao: IUserDao
) {
    suspend fun getAll(): List<UserModel>{
        return dao.getAll().map {
            it.toModel()
        }
    }
    suspend fun deleteAll(){
        dao.deleteAll()
    }

    suspend fun insertUser(user: UserModel){
        dao.insert(user.toEntity())
    }
}