package com.apexbytestudio.entrix.feature.office

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.usecase.GetCurrentLocationUseCase
import com.apexbytestudio.entrix.domain.usecase.ObserveOfficeUseCase
import com.apexbytestudio.entrix.domain.usecase.SaveOfficeUseCase
import com.apexbytestudio.entrix.domain.usecase.SyncGeofencesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OfficeViewModel(
    private val observeOffice: ObserveOfficeUseCase,
    private val saveOffice: SaveOfficeUseCase,
    private val syncGeofences: SyncGeofencesUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
) : ViewModel() {

    private var officeId: Long = Office.NO_ID

    private val _uiState = MutableStateFlow(OfficeUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<OfficeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeOffice().first()?.let { office ->
                officeId = office.id
                _uiState.update {
                    it.copy(
                        name = office.name,
                        latitude = office.latitude.toCoordinateString(),
                        longitude = office.longitude.toCoordinateString(),
                        radiusMeters = office.radiusMeters,
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onLatitudeChange(value: String) = _uiState.update { it.copy(latitude = value, errorMessage = null) }
    fun onLongitudeChange(value: String) = _uiState.update { it.copy(longitude = value, errorMessage = null) }
    fun onRadiusChange(value: Float) = _uiState.update { it.copy(radiusMeters = value) }

    fun onLocationPermissionDenied() = _uiState.update {
        it.copy(errorMessage = "Location permission is needed to use your current location.")
    }

    fun useCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, errorMessage = null) }
            getCurrentLocation()
                .onSuccess { coordinates ->
                    _uiState.update {
                        it.copy(
                            latitude = coordinates.latitude.toCoordinateString(),
                            longitude = coordinates.longitude.toCoordinateString(),
                            isLoadingLocation = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingLocation = false,
                            errorMessage = "Couldn't get your current location.",
                        )
                    }
                }
        }
    }

    fun save() {
        val state = _uiState.value
        val latitude = state.latitude.toDoubleOrNull()
        val longitude = state.longitude.toDoubleOrNull()
        if (latitude == null || longitude == null) {
            _uiState.update { it.copy(errorMessage = "Enter a valid latitude and longitude.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val office = Office(
                id = officeId,
                name = state.name.trim(),
                latitude = latitude,
                longitude = longitude,
                radiusMeters = state.radiusMeters,
            )
            saveOffice(office)
                .onSuccess { saved ->
                    officeId = saved.id
                    syncGeofences()
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(OfficeEvent.Saved)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Couldn't save the office.")
                    }
                }
        }
    }
}

private fun Double.toCoordinateString(): String = String.format(java.util.Locale.US, "%.6f", this)
