package com.apexbytestudio.entrix.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY enterTime DESC")
    fun observeHistory(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE exitTime IS NULL ORDER BY enterTime DESC LIMIT 1")
    fun observeOpenVisit(): Flow<AttendanceEntity?>

    @Query("SELECT * FROM attendance WHERE exitTime IS NULL AND officeId = :officeId LIMIT 1")
    suspend fun getOpenVisit(officeId: Long): AttendanceEntity?

    @Insert
    suspend fun insert(entity: AttendanceEntity): Long

    @Query("UPDATE attendance SET exitTime = :exitTime WHERE id = :id")
    suspend fun closeVisit(id: Long, exitTime: Long)

    @Query("DELETE FROM attendance")
    suspend fun deleteAll()
}
