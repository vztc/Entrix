package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow

class ObserveAttendanceHistoryUseCase(
    private val attendanceRepository: AttendanceRepository,
) {
    operator fun invoke(): Flow<List<Attendance>> = attendanceRepository.observeHistory()
}
