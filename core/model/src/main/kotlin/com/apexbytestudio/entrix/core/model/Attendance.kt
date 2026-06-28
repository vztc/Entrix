package com.apexbytestudio.entrix.core.model

import java.time.Duration
import java.time.Instant

/**
 * A single visit to an [Office].
 *
 * A record is *open* between an entry and the matching exit: while the user is still inside
 * the geofence [exitTime] is `null` and [duration] is therefore unknown. The exit transition
 * closes the record. Duration is derived rather than stored so it can never drift out of sync
 * with the timestamps that are the single source of truth.
 */
data class Attendance(
    val id: Long = NO_ID,
    val officeId: Long,
    val enterTime: Instant,
    val exitTime: Instant? = null,
    val createdTime: Instant,
) {
    val isOpen: Boolean get() = exitTime == null

    val duration: Duration?
        get() = exitTime?.let { Duration.between(enterTime, it) }

    companion object {
        const val NO_ID: Long = 0L
    }
}
