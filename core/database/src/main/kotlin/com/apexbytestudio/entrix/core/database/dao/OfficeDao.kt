package com.apexbytestudio.entrix.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.apexbytestudio.entrix.core.database.entity.OfficeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfficeDao {
    @Query("SELECT * FROM office LIMIT 1")
    fun observeOffice(): Flow<OfficeEntity?>

    @Query("SELECT * FROM office LIMIT 1")
    suspend fun getOffice(): OfficeEntity?

    /** Inserts a new office or updates the existing one; returns the row id. */
    @Upsert
    suspend fun upsert(office: OfficeEntity): Long

    @Query("DELETE FROM office")
    suspend fun deleteAll()
}
