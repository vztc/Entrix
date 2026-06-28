package com.apexbytestudio.entrix.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Typed access to the small amount of app state that does not belong in the attendance table:
 * the tracking toggle and the pending (un-confirmed) geofence entry time.
 */
class EntrixPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    val trackingEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_TRACKING_ENABLED] ?: false }

    suspend fun isTrackingEnabled(): Boolean = trackingEnabled.first()

    suspend fun setTrackingEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_TRACKING_ENABLED] = enabled }
    }

    suspend fun getPendingEnterMillis(): Long? =
        dataStore.data.map { it[KEY_PENDING_ENTER_MILLIS] }.first()

    suspend fun setPendingEnterMillis(millis: Long?) {
        dataStore.edit { prefs ->
            if (millis == null) prefs.remove(KEY_PENDING_ENTER_MILLIS)
            else prefs[KEY_PENDING_ENTER_MILLIS] = millis
        }
    }

    private companion object {
        val KEY_TRACKING_ENABLED = booleanPreferencesKey("tracking_enabled")
        val KEY_PENDING_ENTER_MILLIS = longPreferencesKey("pending_enter_millis")
    }
}
