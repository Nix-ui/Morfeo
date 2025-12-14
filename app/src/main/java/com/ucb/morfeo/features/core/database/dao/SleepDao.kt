package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.datetime.LocalDate

@Dao
interface SleepDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<SleepCore>)

    @Query("DELETE FROM sleep_core")
    suspend fun clearAll()
    @Query("""
    SELECT * FROM sleep_core
    WHERE userEmail = :userEmail AND date = :date
    LIMIT 1
""")
    suspend fun getSleepByDate(userEmail: String, date: LocalDate): SleepCore?

    @Query("""
        SELECT * FROM sleep_core 
        WHERE userEmail = :userEmail 
        AND date BETWEEN :startDate AND :endDate 
        ORDER BY date
    """)
    suspend fun getSleepSessionsBetween(
        userEmail: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SleepCore>
}
