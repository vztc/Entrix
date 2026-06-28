package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.domain.geofence.GeofenceScheduler
import com.apexbytestudio.entrix.domain.repository.OfficeRepository
import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository

/**
 * Reconciles the registered geofence with the desired state. A single idempotent entry point used
 * after enabling/disabling tracking, after the office changes, and on device boot — so the geofence
 * can never drift from "tracking enabled AND an office is configured".
 */
class SyncGeofencesUseCase(
    private val officeRepository: OfficeRepository,
    private val trackingStateRepository: TrackingStateRepository,
    private val geofenceScheduler: GeofenceScheduler,
) {
    suspend operator fun invoke(): Result<Unit> {
        val enabled = trackingStateRepository.isTrackingEnabled()
        val office = officeRepository.getOffice()

        return if (enabled && office != null) {
            geofenceScheduler.register(office)
        } else {
            geofenceScheduler.clear()
        }
    }
}
