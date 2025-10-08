package com.ucb.morfeo.features.welcome.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ucb.morfeo.features.welcome.data.database.Converters.Converters
import com.ucb.morfeo.features.welcome.data.database.dao.IUserDao
import com.ucb.morfeo.features.welcome.data.database.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase(): RoomDatabase() {
    abstract fun userDao(): IUserDao

    companion object{
        @Volatile
        private var Instance: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, AppRoomDatabase::class.java,"morfeo_db")
                    .build()
                    .also { Instance = it}
            }
        }
    }
}