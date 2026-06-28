package com.apexbytestudio.entrix.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.apexbytestudio.entrix.core.model.Coordinates
import com.apexbytestudio.entrix.domain.location.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

/**
 * [LocationProvider] backed by the fused location provider. Requests a fresh fix and falls back to
 * the last known location. Missing permission surfaces as a [SecurityException] captured into a
 * failed [Result], so the caller can prompt instead of crashing.
 */
class FusedLocationProvider(
    private val context: Context,
) : LocationProvider {

    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    // Permission is intentionally not checked here: a missing permission throws SecurityException,
    // which runCatching turns into a failed Result for the caller to handle (e.g. prompt the user).
    @SuppressLint("MissingPermission")
    override suspend fun currentLocation(): Result<Coordinates> = runCatching {
        val cancellation = CancellationTokenSource()
        val location = client
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
            .await()
            ?: client.lastLocation.await()
            ?: error("No location fix available")

        Coordinates(latitude = location.latitude, longitude = location.longitude)
    }
}
