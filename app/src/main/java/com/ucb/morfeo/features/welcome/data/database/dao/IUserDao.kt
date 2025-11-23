package com.ucb.morfeo.features.welcome.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.morfeo.features.welcome.data.database.entity.UserEntity


@Dao
interface IUserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}