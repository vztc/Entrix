package com.apexbytestudio.entrix.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.apexbytestudio.entrix.core.model.CsvExportStatus
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
    val csvExportStatus: Flow<CsvExportStatus> = dataStore.data.map { prefs ->
        CsvExportStatus(
            documentUri = prefs[KEY_CSV_URI],
            fileName = prefs[KEY_CSV_NAME],
            lastWrittenMillis = prefs[KEY_CSV_WRITTEN],
            lastDigest = prefs[KEY_CSV_DIGEST],
            revision = prefs[KEY_CSV_REVISION] ?: 0L,
            writtenRevision = prefs[KEY_CSV_WRITTEN_REVISION] ?: -1L,
            error = prefs[KEY_CSV_ERROR],
        )
    }

    suspend fun linkCsv(uri: String, name: String) {
        dataStore.edit {
            it[KEY_CSV_URI] = uri
            it[KEY_CSV_NAME] = name
            it.remove(KEY_CSV_DIGEST)
            it.remove(KEY_CSV_WRITTEN)
            it.remove(KEY_CSV_WRITTEN_REVISION)
            it.remove(KEY_CSV_ERROR)
        }
    }

    suspend fun unlinkCsv() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_CSV_URI)
            prefs.remove(KEY_CSV_NAME)
            prefs.remove(KEY_CSV_DIGEST)
            prefs.remove(KEY_CSV_WRITTEN)
            prefs.remove(KEY_CSV_WRITTEN_REVISION)
            prefs.remove(KEY_CSV_ERROR)
        }
    }

    suspend fun markCsvChanged() {
        dataStore.edit { it[KEY_CSV_REVISION] = (it[KEY_CSV_REVISION] ?: 0L) + 1L }
    }

    suspend fun markCsvWritten(uri: String, revision: Long, digest: String, writtenAt: Long?) {
        dataStore.edit {
            if (it[KEY_CSV_URI] == uri) {
                it[KEY_CSV_WRITTEN_REVISION] = revision
                it[KEY_CSV_DIGEST] = digest
                if (writtenAt != null) it[KEY_CSV_WRITTEN] = writtenAt
                it.remove(KEY_CSV_ERROR)
            }
        }
    }

    suspend fun setCsvError(message: String) { dataStore.edit { it[KEY_CSV_ERROR] = message } }

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
        val KEY_CSV_URI = stringPreferencesKey("csv_uri")
        val KEY_CSV_NAME = stringPreferencesKey("csv_name")
        val KEY_CSV_WRITTEN = longPreferencesKey("csv_last_written")
        val KEY_CSV_DIGEST = stringPreferencesKey("csv_digest")
        val KEY_CSV_REVISION = longPreferencesKey("csv_revision")
        val KEY_CSV_WRITTEN_REVISION = longPreferencesKey("csv_written_revision")
        val KEY_CSV_ERROR = stringPreferencesKey("csv_error")
        val KEY_TRACKING_ENABLED = booleanPreferencesKey("tracking_enabled")
        val KEY_PENDING_ENTER_MILLIS = longPreferencesKey("pending_enter_millis")
    }
}
