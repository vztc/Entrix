package com.apexbytestudio.entrix.core.database.di

import androidx.room.Room
import com.apexbytestudio.entrix.core.database.EntrixDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            EntrixDatabase::class.java,
            EntrixDatabase.NAME,
        ).build()
    }
    single { get<EntrixDatabase>().officeDao() }
    single { get<EntrixDatabase>().attendanceDao() }
}
