package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import kotlinx.coroutines.flow.Flow

class AutomationConfigRepository(val automationConfigDao: AutomationConfigDao) {

    fun getAutomationConfig(packageName: String): Flow<AutomationConfig?> {
        return automationConfigDao.getAutomationConfig(packageName)
    }

    fun getAllAutomationConfigs(): Flow<List<AutomationConfig>> {
        return automationConfigDao.getAllAutomationConfigs()
    }

    suspend fun insertAutomationConfig(config: AutomationConfig) {
        automationConfigDao.insertAutomationConfig(config)
    }
}
