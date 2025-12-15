package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface SleepDao {
    @Insert
    suspend fun insertSleepCore(sleepCore: SleepCore): Long

    @Update
    suspend fun updateSleepCore(sleepCore: SleepCore)

    @Query("SELECT * FROM sleep_core WHERE id =:id")
    suspend fun getSleepCoreById(id: Int): SleepCore?

    @Query("SELECT * FROM sleep_core WHERE userEmail= :email ORDER BY date DESC")
    fun getSleepHistory(email: String): Flow<List<SleepCore>>

    @Query("SELECT * FROM sleep_core WHERE userEmail= :email AND date= :date")
    suspend fun getSleepCoreByDate(email: String, date:String): SleepCore?

    @Query("DELETE FROM sleep_core WHERE id=:id")
    suspend fun deleteSleepCore(id:Int)

    @Query("SELECT AVG(sleepScore) as avgScore, AVG(sleepDuration) as avgDuration, AVG(deepSleepPercentage) as avgDeepSleep, COUNT(*) as totalNights FROM sleep_core WHERE userEmail= :email")
    suspend fun getSleepStatistics(email: String): SleepStatistics?

    data class SleepStatistics(
        val avgScore: Float,
        val avgDuration: Float,
        val avgDeepSleep: Float,
        val totalNights: Int
    )

    @Query("SELECT * FROM sleep_core WHERE userEmail = :userEmail AND date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getSleepSessionsBetween(userEmail: String, startDate: LocalDate, endDate: LocalDate): List<SleepCore>
}