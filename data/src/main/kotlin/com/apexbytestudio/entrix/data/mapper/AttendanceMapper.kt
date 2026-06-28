package com.apexbytestudio.entrix.data.mapper

import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import com.apexbytestudio.entrix.core.model.Attendance
import java.time.Instant

fun AttendanceEntity.toModel(): Attendance = Attendance(
    id = id,
    officeId = officeId,
    enterTime = Instant.ofEpochMilli(enterTime),
    exitTime = exitTime?.let(Instant::ofEpochMilli),
    createdTime = Instant.ofEpochMilli(createdTime),
)

fun Attendance.toEntity(): AttendanceEntity = AttendanceEntity(
    id = id,
    officeId = officeId,
    enterTime = enterTime.toEpochMilli(),
    exitTime = exitTime?.toEpochMilli(),
    createdTime = createdTime.toEpochMilli(),
)
