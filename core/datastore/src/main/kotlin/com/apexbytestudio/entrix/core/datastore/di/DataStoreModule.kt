package com.apexbytestudio.entrix.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.apexbytestudio.entrix.core.datastore.EntrixPreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "entrix_prefs")

val dataStoreModule = module {
    single { EntrixPreferencesDataSource(androidContext().dataStore) }
}
