package com.apexbytestudio.entrix.feature.attendance

data class AttendanceUiState(
    val isLoading: Boolean = true,
    val days: List<AttendanceDayUi> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && days.isEmpty()
}

data class AttendanceDayUi(
    val date: String,
    val visits: List<AttendanceVisitUi>,
)

data class AttendanceVisitUi(
    val id: Long,
    val entry: String,
    val exit: String,
    val duration: String,
)
