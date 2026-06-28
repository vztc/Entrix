package com.apexbytestudio.entrix.feature.dashboard.di

import com.apexbytestudio.entrix.feature.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardViewModel)
}
