package com.apexbytestudio.entrix.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Persisted tracking state that lives outside the attendance table:
 *  - whether geofence tracking is enabled, and
 *  - the cached candidate entry time between an ENTER and the confirming DWELL.
 *
 * The pending entry time is persisted (not held in memory) so an accurate entry time survives
 * process death between the two transitions.
 */
interface TrackingStateRepository {
    val trackingEnabled: Flow<Boolean>

    suspend fun isTrackingEnabled(): Boolean

    suspend fun setTrackingEnabled(enabled: Boolean)

    suspend fun getPendingEnterTime(): Instant?

    /** Sets the cached candidate entry time; pass `null` to clear it. */
    suspend fun setPendingEnterTime(time: Instant?)
}
