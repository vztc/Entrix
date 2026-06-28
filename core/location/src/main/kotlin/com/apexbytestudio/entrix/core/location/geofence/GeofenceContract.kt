package com.apexbytestudio.entrix.core.location.geofence

import com.apexbytestudio.entrix.core.model.GeofenceTransition
import com.google.android.gms.location.Geofence

internal object GeofenceContract {
    /** ~2 minutes inside the region before a DWELL fires — filters drive-bys and boundary jitter. */
    const val LOITERING_DELAY_MILLIS = 120_000
}

/** Maps a Play Services transition constant to the domain [GeofenceTransition], or null if unhandled. */
internal fun Int.toGeofenceTransition(): GeofenceTransition? = when (this) {
    Geofence.GEOFENCE_TRANSITION_ENTER -> GeofenceTransition.Enter
    Geofence.GEOFENCE_TRANSITION_DWELL -> GeofenceTransition.Dwell
    Geofence.GEOFENCE_TRANSITION_EXIT -> GeofenceTransition.Exit
    else -> null
}

class GeofenceUnavailableException(message: String) : Exception(message)
