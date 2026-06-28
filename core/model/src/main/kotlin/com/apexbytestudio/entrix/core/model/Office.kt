package com.apexbytestudio.entrix.core.model

/**
 * A geofenced location the user wants to track attendance for.
 *
 * The MVP only ever persists a single office, but the model carries a stable [id] so the
 * data layer and a future multi-office feature can reference offices without a schema change.
 */
data class Office(
    val id: Long = NO_ID,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
) {
    companion object {
        const val NO_ID: Long = 0L
    }
}
