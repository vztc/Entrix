package com.apexbytestudio.entrix.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "office")
data class OfficeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
)
