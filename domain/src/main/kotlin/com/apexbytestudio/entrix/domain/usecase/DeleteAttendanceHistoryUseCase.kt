package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.domain.repository.AttendanceRepository

class DeleteAttendanceHistoryUseCase(
    private val attendanceRepository: AttendanceRepository,
) {
    suspend operator fun invoke() = attendanceRepository.deleteAll()
}
