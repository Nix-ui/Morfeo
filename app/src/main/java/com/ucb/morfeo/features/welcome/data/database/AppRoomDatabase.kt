package com.ucb.morfeo.features.welcome.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.welcome.data.database.Converters.Converters
import com.ucb.morfeo.features.welcome.data.database.dao.IUserDao
import com.ucb.morfeo.features.welcome.data.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, SleepCore::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun userDao(): IUserDao
    abstract fun sleepDao(): SleepDao

    companion object {
        @Volatile
        private var Instance: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppRoomDatabase::class.java, "morfeo_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}