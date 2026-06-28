package com.apexbytestudio.entrix.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.apexbytestudio.entrix.core.database.dao.AttendanceDao
import com.apexbytestudio.entrix.core.database.dao.OfficeDao
import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import com.apexbytestudio.entrix.core.database.entity.OfficeEntity

@Database(
    entities = [OfficeEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class EntrixDatabase : RoomDatabase() {
    abstract fun officeDao(): OfficeDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        const val NAME = "entrix.db"
    }
}
