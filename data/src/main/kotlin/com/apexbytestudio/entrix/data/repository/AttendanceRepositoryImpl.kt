package com.apexbytestudio.entrix.data.repository

import com.apexbytestudio.entrix.core.database.dao.AttendanceDao
import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.data.mapper.toModel
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class AttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao,
) : AttendanceRepository {

    override fun observeHistory(): Flow<List<Attendance>> =
        attendanceDao.observeHistory().map { entities -> entities.map { it.toModel() } }

    override fun observeOpenVisit(): Flow<Attendance?> =
        attendanceDao.observeOpenVisit().map { it?.toModel() }

    override suspend fun getOpenVisit(officeId: Long): Attendance? =
        attendanceDao.getOpenVisit(officeId)?.toModel()

    override suspend fun openVisit(officeId: Long, enterTime: Instant, createdTime: Instant): Long =
        attendanceDao.insert(
            AttendanceEntity(
                officeId = officeId,
                enterTime = enterTime.toEpochMilli(),
                exitTime = null,
                createdTime = createdTime.toEpochMilli(),
            ),
        )

    override suspend fun closeVisit(id: Long, exitTime: Instant) =
        attendanceDao.closeVisit(id, exitTime.toEpochMilli())

    override suspend fun deleteAll() = attendanceDao.deleteAll()
}
