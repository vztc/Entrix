package com.apexbytestudio.entrix.core.location.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.apexbytestudio.entrix.domain.usecase.SyncGeofencesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Geofences do not survive a reboot, so re-register them once the device finishes booting.
 * [SyncGeofencesUseCase] is idempotent and no-ops when tracking is off or no office is configured.
 */
class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val syncGeofences: SyncGeofencesUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                syncGeofences()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to restore geofences after boot", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "BootReceiver"
    }
}
