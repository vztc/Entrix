package com.apexbytestudio.entrix.domain.di

import com.apexbytestudio.entrix.domain.usecase.DeleteAttendanceHistoryUseCase
import com.apexbytestudio.entrix.domain.usecase.GetCurrentLocationUseCase
import com.apexbytestudio.entrix.domain.usecase.ObserveAttendanceHistoryUseCase
import com.apexbytestudio.entrix.domain.usecase.ObserveDashboardUseCase
import com.apexbytestudio.entrix.domain.usecase.ObserveOfficeUseCase
import com.apexbytestudio.entrix.domain.usecase.ObserveTrackingEnabledUseCase
import com.apexbytestudio.entrix.domain.usecase.RecordGeofenceTransitionUseCase
import com.apexbytestudio.entrix.domain.usecase.SaveOfficeUseCase
import com.apexbytestudio.entrix.domain.usecase.SetTrackingEnabledUseCase
import com.apexbytestudio.entrix.domain.usecase.SyncGeofencesUseCase
import org.koin.dsl.module

/** Use cases are stateless; declaring them as `factory` keeps them cheap and side-effect free. */
val domainModule = module {
    factory { SaveOfficeUseCase(get()) }
    factory { ObserveOfficeUseCase(get()) }
    factory { ObserveAttendanceHistoryUseCase(get()) }
    factory { DeleteAttendanceHistoryUseCase(get()) }
    factory { SyncGeofencesUseCase(get(), get(), get()) }
    factory { SetTrackingEnabledUseCase(get(), get()) }
    factory { ObserveDashboardUseCase(get(), get(), get()) }
    factory { RecordGeofenceTransitionUseCase(get(), get()) }
    factory { GetCurrentLocationUseCase(get()) }
    factory { ObserveTrackingEnabledUseCase(get()) }
}
