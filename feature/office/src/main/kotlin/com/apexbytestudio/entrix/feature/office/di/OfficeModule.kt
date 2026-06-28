package com.apexbytestudio.entrix.feature.office.di

import com.apexbytestudio.entrix.feature.office.OfficeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val officeModule = module {
    viewModelOf(::OfficeViewModel)
}
