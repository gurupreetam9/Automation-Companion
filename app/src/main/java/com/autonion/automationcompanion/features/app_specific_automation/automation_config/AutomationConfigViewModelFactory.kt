package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class AutomationConfigViewModelFactory(
    private val repository: AutomationConfigRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AutomationConfigViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return AutomationConfigViewModel(savedStateHandle, repository.automationConfigDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
