package com.apexbytestudio.entrix

import android.app.Application
import com.apexbytestudio.entrix.core.common.di.coroutinesModule
import com.apexbytestudio.entrix.core.location.di.locationModule
import com.apexbytestudio.entrix.data.di.dataModule
import com.apexbytestudio.entrix.domain.di.domainModule
import com.apexbytestudio.entrix.feature.attendance.di.attendanceModule
import com.apexbytestudio.entrix.feature.dashboard.di.dashboardModule
import com.apexbytestudio.entrix.feature.office.di.officeModule
import com.apexbytestudio.entrix.feature.settings.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.android.ext.android.get
import com.apexbytestudio.entrix.data.export.AutomaticCsvExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EntrixApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@EntrixApplication)
            modules(
                coroutinesModule,
                domainModule,
                dataModule,
                locationModule,
                dashboardModule,
                attendanceModule,
                officeModule,
                settingsModule,
            )
        }
        applicationScope.launch { get<AutomaticCsvExporter>().resumeAutomaticExport() }
    }
}
