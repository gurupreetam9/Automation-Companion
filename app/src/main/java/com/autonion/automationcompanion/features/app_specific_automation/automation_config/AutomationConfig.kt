package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automation_configs")
data class AutomationConfig(
    @PrimaryKey
    val packageName: String,
    val audioSettings: AudioSettings,
    val displaySettings: DisplaySettings
)
