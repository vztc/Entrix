package com.apexbytestudio.entrix.data.export

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.apexbytestudio.entrix.core.datastore.EntrixPreferencesDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class CsvExportScheduler(
    private val context: Context,
    private val preferences: EntrixPreferencesDataSource,
) {
    private val manager get() = WorkManager.getInstance(context)

    suspend fun dataChanged() {
        // A cloud export failure must never turn a committed attendance record into a failure.
        try {
            preferences.markCsvChanged()
            if (preferences.csvExportStatus.first().isLinked) enqueue()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("EntrixCsv", "Could not schedule CSV update", e)
        }
    }

    fun enqueue() {
        manager.enqueueUniqueWork(
            IMMEDIATE,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            OneTimeWorkRequestBuilder<CsvExportWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build(),
        )
        ensurePeriodic()
    }

    fun ensurePeriodic() {
        manager.enqueueUniquePeriodicWork(
            PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<CsvExportWorker>(15, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build(),
        )
    }

    fun cancel() {
        manager.cancelUniqueWork(IMMEDIATE)
        manager.cancelUniqueWork(PERIODIC)
    }

    private companion object {
        const val IMMEDIATE = "entrix-csv-update"
        const val PERIODIC = "entrix-csv-reconcile"
    }
}
