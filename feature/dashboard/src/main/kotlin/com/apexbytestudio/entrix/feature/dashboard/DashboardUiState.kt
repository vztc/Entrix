package com.apexbytestudio.entrix.feature.dashboard

data class DashboardUiState(
    val isLoading: Boolean = true,
    val officeConfigured: Boolean = false,
    val officeName: String? = null,
    val insideOffice: Boolean = false,
    val trackingEnabled: Boolean = false,
    val todayEnter: String? = null,
    val todayExit: String? = null,
    val todayDuration: String? = null,
)
