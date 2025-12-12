package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.datetime.LocalDate

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_core WHERE userEmail = :userEmail AND date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getSleepSessionsBetween(userEmail: String, startDate: LocalDate, endDate: LocalDate): List<SleepCore>
}