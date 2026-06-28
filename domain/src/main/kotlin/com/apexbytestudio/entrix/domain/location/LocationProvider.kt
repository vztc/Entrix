package com.apexbytestudio.entrix.domain.location

import com.apexbytestudio.entrix.core.model.Coordinates

/**
 * One-shot access to the device's current location, abstracted from Play Services so the office
 * picker can offer a "use my location" convenience without the domain knowing about Android.
 * Implementations return a failed [Result] when permission is missing or no fix is available.
 */
interface LocationProvider {
    suspend fun currentLocation(): Result<Coordinates>
}
