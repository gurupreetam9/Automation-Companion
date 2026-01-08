package com.autonion.automationcompanion.features.app_specific_automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfig
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(repository: AutomationConfigRepository) : ViewModel() {

    val automationConfigs: StateFlow<List<AutomationConfig>> = repository.getAllAutomationConfigs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
