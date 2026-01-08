package com.autonion.automationcompanion.features.app_specific_automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigRepository


class MainViewModelFactory(private val repository: AutomationConfigRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
