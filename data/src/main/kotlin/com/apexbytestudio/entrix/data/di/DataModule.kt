package com.apexbytestudio.entrix.data.di

import com.apexbytestudio.entrix.core.database.di.databaseModule
import com.apexbytestudio.entrix.core.datastore.di.dataStoreModule
import com.apexbytestudio.entrix.data.repository.AttendanceRepositoryImpl
import com.apexbytestudio.entrix.data.repository.OfficeRepositoryImpl
import com.apexbytestudio.entrix.data.repository.TrackingStateRepositoryImpl
import com.apexbytestudio.entrix.domain.repository.AttendanceRepository
import com.apexbytestudio.entrix.domain.repository.OfficeRepository
import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Binds repository implementations to their domain interfaces and pulls in the database/datastore
 * modules so a single `dataModule` is all the app needs to load for the whole data layer.
 */
val dataModule = module {
    includes(databaseModule, dataStoreModule)

    singleOf(::OfficeRepositoryImpl) bind OfficeRepository::class
    singleOf(::AttendanceRepositoryImpl) bind AttendanceRepository::class
    singleOf(::TrackingStateRepositoryImpl) bind TrackingStateRepository::class
}
