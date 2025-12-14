package com.ucb.morfeo.features.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.datetime.LocalDate

@Dao
interface SleepDao {

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
