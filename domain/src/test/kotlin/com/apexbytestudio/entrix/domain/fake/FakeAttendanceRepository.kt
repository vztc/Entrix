package com.apexbytestudio.entrix.domain.fake

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

class FakeAttendanceRepository : AttendanceRepository {
    private val records = MutableStateFlow<List<Attendance>>(emptyList())
    private var nextId = 1L

    override fun observeHistory(): Flow<List<Attendance>> =
        records.map { list -> list.sortedByDescending { it.enterTime } }

    override fun observeOpenVisit(): Flow<Attendance?> =
        records.map { list -> list.firstOrNull { it.isOpen } }

    override suspend fun getOpenVisit(officeId: Long): Attendance? =
        records.value.firstOrNull { it.isOpen && it.officeId == officeId }

    override suspend fun openVisit(officeId: Long, enterTime: Instant, createdTime: Instant): Long {
        val id = nextId++
        records.value = records.value + Attendance(
            id = id,
            officeId = officeId,
            enterTime = enterTime,
            exitTime = null,
            createdTime = createdTime,
        )
        return id
    }

    override suspend fun closeVisit(id: Long, exitTime: Instant) {
        records.value = records.value.map {
            if (it.id == id) it.copy(exitTime = exitTime) else it
        }
    }

    override suspend fun deleteAll() {
        records.value = emptyList()
    }

    // Test helpers
    fun current(): List<Attendance> = records.value
    fun seed(vararg attendance: Attendance) {
        records.value = attendance.toList()
        nextId = (attendance.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}
