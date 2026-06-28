package com.apexbytestudio.entrix.feature.settings.di

import com.apexbytestudio.entrix.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
