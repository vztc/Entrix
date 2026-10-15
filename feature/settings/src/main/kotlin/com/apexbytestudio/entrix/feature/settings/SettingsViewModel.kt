package com.apexbytestudio.entrix.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexbytestudio.entrix.domain.usecase.ObserveTrackingEnabledUseCase
import com.apexbytestudio.entrix.domain.usecase.SetTrackingEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.apexbytestudio.entrix.core.model.CsvExportStatus
import com.apexbytestudio.entrix.domain.repository.AutomaticCsvExportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    observeTrackingEnabled: ObserveTrackingEnabledUseCase,
    private val setTrackingEnabled: SetTrackingEnabledUseCase,
    private val csvExport: AutomaticCsvExportRepository,
) : ViewModel() {
    val csvStatus = csvExport.observeStatus().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000L), CsvExportStatus(),
    )
    private val _csvBusy = MutableStateFlow(false)
    val csvBusy = _csvBusy.asStateFlow()
    private val _csvMessage = MutableStateFlow<String?>(null)
    val csvMessage = _csvMessage.asStateFlow()

    fun linkCsv(uri: String) {
        viewModelScope.launch {
            _csvBusy.value = true
            _csvMessage.value = null
            csvExport.linkDocument(uri).onFailure {
                _csvMessage.value = when (it) {
                    is SecurityException -> "Permanent file access was not granted. For OneDrive, connect through RSAF."
                    is IllegalArgumentException -> it.message
                    else -> "Could not link the CSV. Check the storage provider and try again."
                }
            }
            _csvBusy.value = false
        }
    }

    fun disconnectCsv() {
        viewModelScope.launch {
            csvExport.disconnect()
            _csvMessage.value = null
        }
    }

    val trackingEnabled: StateFlow<Boolean> = observeTrackingEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = false,
        )

    fun setTracking(enabled: Boolean) {
        viewModelScope.launch { setTrackingEnabled(enabled) }
    }
}
