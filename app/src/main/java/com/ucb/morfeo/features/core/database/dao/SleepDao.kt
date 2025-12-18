package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
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

    @Query("""
        SELECT * FROM sleep_core 
        WHERE userEmail = :userEmail 
        ORDER BY wakeTime DESC 
        LIMIT 1
    """)
    suspend fun getLastSleepByEmail(userEmail: String): SleepCore?

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

    @Query("SELECT * FROM sleep_core WHERE userEmail = :userEmail ORDER BY date")
    suspend fun getAllSleepSessions(userEmail: String): List<SleepCore>

    // 🌱 Seed inicial (datos demo) -> NO duplica si hay conflictos (si existieran)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<SleepCore>)

    // ✅ Insert de 1 elemento (para registro manual)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(item: SleepCore): Long

    // ✅ Borra un registro existente por usuario + fecha (para evitar duplicados por día)
    @Query("""
        DELETE FROM sleep_core
        WHERE userEmail = :userEmail AND date = :date
    """)
    suspend fun deleteByUserAndDate(userEmail: String, date: LocalDate)

    // ✅ Upsert real por (userEmail, date): borra y vuelve a insertar
    @Transaction
    suspend fun upsertByUserAndDate(item: SleepCore) {
        deleteByUserAndDate(item.userEmail, item.date)
        insertOne(item.copy(id = 0)) // id autogenerado
    }

    @Query("DELETE FROM sleep_core")
    suspend fun clearAll()

    @Query("""
        SELECT * FROM sleep_core
        WHERE userEmail = :userEmail AND date = :date
        LIMIT 1
    """)
    suspend fun getSleepByDate(
        userEmail: String,
        date: LocalDate
    ): SleepCore?

}