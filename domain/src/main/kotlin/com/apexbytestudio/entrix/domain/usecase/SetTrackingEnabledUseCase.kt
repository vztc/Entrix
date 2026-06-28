package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository

/**
 * Persists the tracking preference and then [syncGeofences] so geofence registration always follows
 * the toggle. Returns the registration result so the UI can surface failures (e.g. missing
 * permission) while the preference itself is already stored.
 */
class SetTrackingEnabledUseCase(
    private val trackingStateRepository: TrackingStateRepository,
    private val syncGeofences: SyncGeofencesUseCase,
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> {
        trackingStateRepository.setTrackingEnabled(enabled)
        return syncGeofences()
    }
}
