package com.apexbytestudio.entrix.core.location.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.apexbytestudio.entrix.domain.usecase.RecordGeofenceTransitionUseCase
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant

/**
 * Receives geofence transitions and hands them to [RecordGeofenceTransitionUseCase]. Work is done in
 * [goAsync] so the short DB write completes after `onReceive` returns, without a foreground service.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val recordTransition: RecordGeofenceTransitionUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e(TAG, "Geofencing error code=${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition.toGeofenceTransition() ?: return
        val triggering = event.triggeringGeofences.orEmpty()
        if (triggering.isEmpty()) return

        val occurredAt = Instant.now()
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                triggering.forEach { geofence ->
                    val officeId = geofence.requestId.toLongOrNull() ?: return@forEach
                    recordTransition(officeId, transition, occurredAt)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to record transition", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "GeofenceReceiver"
    }
}
