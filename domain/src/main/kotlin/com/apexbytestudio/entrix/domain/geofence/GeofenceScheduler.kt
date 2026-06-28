package com.apexbytestudio.entrix.domain.geofence

import com.apexbytestudio.entrix.core.model.Office

/**
 * Abstraction over the platform geofencing engine. Defined in the domain so use cases can drive
 * registration without depending on Android / Play Services; the implementation lives in
 * `:core:location`. Implementations must be idempotent — registering replaces any prior geofence.
 */
interface GeofenceScheduler {
    suspend fun register(office: Office): Result<Unit>

    suspend fun clear(): Result<Unit>
}
