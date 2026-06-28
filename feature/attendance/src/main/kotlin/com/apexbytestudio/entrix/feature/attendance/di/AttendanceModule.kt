package com.apexbytestudio.entrix.feature.attendance.di

import com.apexbytestudio.entrix.feature.attendance.AttendanceViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val attendanceModule = module {
    viewModelOf(::AttendanceViewModel)
}
