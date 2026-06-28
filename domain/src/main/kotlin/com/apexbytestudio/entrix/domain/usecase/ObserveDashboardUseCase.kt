package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.domain.model.DashboardSnapshot
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import com.apexbytestudio.entrix.domain.repository.OfficeRepository
import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/**
 * Combines office, open-visit, history and tracking streams into a single [DashboardSnapshot].
 * The [clock] is injected so "today" is deterministic in tests.
 */
class ObserveDashboardUseCase(
    private val officeRepository: OfficeRepository,
    private val attendanceRepository: AttendanceRepository,
    private val trackingStateRepository: TrackingStateRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    operator fun invoke(): Flow<DashboardSnapshot> = combine(
        officeRepository.observeOffice(),
        attendanceRepository.observeOpenVisit(),
        attendanceRepository.observeHistory(),
        trackingStateRepository.trackingEnabled,
    ) { office, openVisit, history, trackingEnabled ->
        DashboardSnapshot(
            office = office,
            openVisit = openVisit,
            todayVisits = history.filter { it.startedOn(today()) },
            trackingEnabled = trackingEnabled,
        )
    }

    private fun today(): LocalDate = LocalDate.now(clock)

    private fun Attendance.startedOn(date: LocalDate): Boolean {
        val zone: ZoneId = clock.zone
        return enterTime.atZone(zone).toLocalDate() == date
    }
}
