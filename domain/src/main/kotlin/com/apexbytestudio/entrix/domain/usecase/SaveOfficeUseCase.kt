package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.repository.OfficeRepository

/**
 * Validates and persists an office. Validation lives here (not in the UI) so any caller —
 * screen, test, or a future import feature — gets the same guarantees.
 */
class SaveOfficeUseCase(
    private val officeRepository: OfficeRepository,
) {
    suspend operator fun invoke(office: Office): Result<Office> {
        val name = office.name.trim()
        if (name.isEmpty()) {
            return Result.failure(InvalidOfficeException("Office name must not be blank"))
        }
        if (office.latitude !in -90.0..90.0 || office.longitude !in -180.0..180.0) {
            return Result.failure(InvalidOfficeException("Coordinates are out of range"))
        }
        if (office.radiusMeters !in MIN_RADIUS_METERS..MAX_RADIUS_METERS) {
            return Result.failure(
                InvalidOfficeException("Radius must be between $MIN_RADIUS_METERS and $MAX_RADIUS_METERS metres"),
            )
        }

        return runCatching { officeRepository.saveOffice(office.copy(name = name)) }
    }

    companion object {
        // Below ~100 m geofences become unreliable on most hardware; keep a sane upper bound too.
        const val MIN_RADIUS_METERS = 100f
        const val MAX_RADIUS_METERS = 2_000f
    }
}

class InvalidOfficeException(message: String) : IllegalArgumentException(message)
