package com.apexbytestudio.entrix.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Timestamps are stored as epoch milliseconds (UTC), keeping the table timezone-agnostic; the
 * data layer maps them to/from `java.time.Instant`. A null [exitTime] marks an open visit.
 */
@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = OfficeEntity::class,
            parentColumns = ["id"],
            childColumns = ["officeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("officeId"), Index("exitTime")],
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val officeId: Long,
    val enterTime: Long,
    val exitTime: Long?,
    val createdTime: Long,
)
