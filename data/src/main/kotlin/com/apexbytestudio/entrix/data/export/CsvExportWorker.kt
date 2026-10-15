package com.apexbytestudio.entrix.data.export

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CsvExportWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters), KoinComponent {
    private val exporter: AutomaticCsvExporter by inject()

    override suspend fun doWork(): Result {
        return try {
            exporter.sync()
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            exporter.reportFailure(e)
            if (e is SecurityException || e is IllegalArgumentException || runAttemptCount >= 5) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}
