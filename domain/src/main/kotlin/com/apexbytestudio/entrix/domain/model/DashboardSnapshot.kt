package com.apexbytestudio.entrix.domain.model

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.core.model.Office
import java.time.Duration
import java.time.Instant

/**
 * Everything the dashboard needs in a single immutable snapshot. Derived values are exposed as
 * computed properties so the screen never re-implements the rules. Live "time since entry" for an
 * open visit is intentionally *not* here — that ticks in the presentation layer against `now`.
 */
data class DashboardSnapshot(
    val office: Office?,
    val openVisit: Attendance?,
    val todayVisits: List<Attendance>,
    val trackingEnabled: Boolean,
) {
    val isOfficeConfigured: Boolean get() = office != null

    val isInsideOffice: Boolean get() = openVisit != null

    val todayFirstEnter: Instant? get() = todayVisits.minByOrNull { it.enterTime }?.enterTime

    val todayLastExit: Instant? get() = todayVisits.mapNotNull { it.exitTime }.maxOrNull()

    /** Sum of completed visit durations today. The currently open visit is excluded. */
    val todayCompletedDuration: Duration
        get() = todayVisits.fold(Duration.ZERO) { acc, visit ->
            acc + (visit.duration ?: Duration.ZERO)
        }

    companion object {
        val Empty = DashboardSnapshot(
            office = null,
            openVisit = null,
            todayVisits = emptyList(),
            trackingEnabled = false,
        )
    }
}
