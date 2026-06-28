package com.apexbytestudio.entrix.core.location.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.geofence.GeofenceScheduler
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

/**
 * [GeofenceScheduler] backed by the Play Services [com.google.android.gms.location.GeofencingClient].
 *
 * Registration is idempotent: the previous geofence is removed before the new one is added. Missing
 * location permission surfaces as a [SecurityException] captured into a failed [Result], so callers
 * can prompt the user instead of crashing.
 */
class PlayGeofenceScheduler(
    private val context: Context,
) : GeofenceScheduler {

    private val geofencingClient by lazy { LocationServices.getGeofencingClient(context) }

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
    }

    // Permission is intentionally not checked here: a missing permission throws SecurityException,
    // which runCatching turns into a failed Result for the caller to handle (e.g. prompt the user).
    @SuppressLint("MissingPermission")
    override suspend fun register(office: Office): Result<Unit> {
        if (!isPlayServicesAvailable()) {
            return Result.failure(GeofenceUnavailableException("Google Play services are unavailable"))
        }

        val geofence = Geofence.Builder()
            .setRequestId(office.id.toString())
            .setCircularRegion(office.latitude, office.longitude, office.radiusMeters)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_DWELL or
                    Geofence.GEOFENCE_TRANSITION_EXIT,
            )
            .setLoiteringDelay(GeofenceContract.LOITERING_DELAY_MILLIS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            // ENTER caches the entry time; DWELL opens a visit if the user is already inside at registration.
            .setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL,
            )
            .addGeofence(geofence)
            .build()

        return runCatching {
            geofencingClient.removeGeofences(pendingIntent).await()
            geofencingClient.addGeofences(request, pendingIntent).await()
            Unit
        }
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(pendingIntent).await()
        Unit
    }

    private fun isPlayServicesAvailable(): Boolean =
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    private companion object {
        const val REQUEST_CODE = 0
    }
}
