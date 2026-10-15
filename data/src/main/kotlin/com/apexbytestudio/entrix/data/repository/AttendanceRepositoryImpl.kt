package com.apexbytestudio.entrix.data.repository

import com.apexbytestudio.entrix.core.database.dao.AttendanceDao
import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.data.mapper.toModel
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import com.apexbytestudio.entrix.data.export.CsvExportScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class AttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao,
    private val csvExport: CsvExportScheduler,
) : AttendanceRepository {

    override fun observeHistory(): Flow<List<Attendance>> =
        attendanceDao.observeHistory().map { entities -> entities.map { it.toModel() } }

    override fun observeOpenVisit(): Flow<Attendance?> =
        attendanceDao.observeOpenVisit().map { it?.toModel() }

    override suspend fun getOpenVisit(officeId: Long): Attendance? =
        attendanceDao.getOpenVisit(officeId)?.toModel()

    override suspend fun openVisit(officeId: Long, enterTime: Instant, createdTime: Instant): Long {
        val id = attendanceDao.insert(
            AttendanceEntity(
                officeId = officeId,
                enterTime = enterTime.toEpochMilli(),
                exitTime = null,
                createdTime = createdTime.toEpochMilli(),
            ),
        )
        csvExport.dataChanged()
        return id
    }

    override suspend fun closeVisit(id: Long, exitTime: Instant) {
        attendanceDao.closeVisit(id, exitTime.toEpochMilli())
        csvExport.dataChanged()
    }

    override suspend fun deleteAll() {
        attendanceDao.deleteAll()
        csvExport.dataChanged()
    }
}
