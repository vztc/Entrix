package com.apexbytestudio.entrix.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexbytestudio.entrix.domain.model.DashboardSnapshot
import com.apexbytestudio.entrix.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DashboardViewModel(
    observeDashboard: ObserveDashboardUseCase,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = observeDashboard()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DashboardUiState(isLoading = true),
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

private val timeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

private fun DashboardSnapshot.toUiState(): DashboardUiState = DashboardUiState(
    isLoading = false,
    officeConfigured = isOfficeConfigured,
    officeName = office?.name,
    insideOffice = isInsideOffice,
    trackingEnabled = trackingEnabled,
    todayEnter = todayFirstEnter?.let(timeFormatter::format),
    todayExit = todayLastExit?.let(timeFormatter::format),
    todayDuration = todayCompletedDuration.takeIf { !it.isZero }?.formatHoursMinutes(),
)

private fun Duration.formatHoursMinutes(): String {
    val hours = toHours()
    val minutes = toMinutes() % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
