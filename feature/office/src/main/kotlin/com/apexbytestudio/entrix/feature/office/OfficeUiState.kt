package com.apexbytestudio.entrix.feature.office

data class OfficeUiState(
    val name: String = "My Office",
    val latitude: String = "",
    val longitude: String = "",
    val radiusMeters: Float = 150f,
    val isLoadingLocation: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() &&
            latitude.toDoubleOrNull() != null &&
            longitude.toDoubleOrNull() != null &&
            !isSaving
}

sealed interface OfficeEvent {
    data object Saved : OfficeEvent
}
