package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutomationConfigViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val automationConfigDao: AutomationConfigDao
) : ViewModel() {

    val packageName: String? = savedStateHandle["packageName"]

    private val _audioSettings = MutableStateFlow(AudioSettings())
    val audioSettings = _audioSettings.asStateFlow()

    private val _displaySettings = MutableStateFlow(DisplaySettings())
    val displaySettings = _displaySettings.asStateFlow()

    init {
        packageName?.let { loadAutomationConfig(it) }
    }

    fun onAudioSettingsChanged(settings: AudioSettings) {
        _audioSettings.update { settings }
    }

    fun onDisplaySettingsChanged(settings: DisplaySettings) {
        _displaySettings.update { settings }
    }

    fun saveConfiguration() {
        viewModelScope.launch {
            val config = AutomationConfig(
                packageName = packageName ?: return@launch,
                audioSettings = audioSettings.value,
                displaySettings = displaySettings.value
            )
            automationConfigDao.insertAutomationConfig(config)
        }
    }

    private fun loadAutomationConfig(packageName: String) {
        viewModelScope.launch {
            automationConfigDao.getAutomationConfig(packageName).firstOrNull()?.let {
                _audioSettings.value = it.audioSettings
                _displaySettings.value = it.displaySettings
            }
        }
    }
}
