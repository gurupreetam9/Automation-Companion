package com.autonion.automationcompanion.features.app_specific_automation.automation_config

// A sealed interface to represent all possible automation types
sealed interface AutomationSetting {
    val isEnabled: Boolean
}

data class AudioSettings(
    override val isEnabled: Boolean = false,
    val mediaVolume: Int? = null,
    val ringVolume: Int? = null,
    val dndMode: DndMode? = null
) : AutomationSetting

data class DisplaySettings(
    override val isEnabled: Boolean = false,
    val brightness: Int? = null, // 0 to 100
    val screenTimeout: Long? = null, // in milliseconds
    val autoRotate: Boolean? = null
) : AutomationSetting

enum class DndMode {
    PRIORITY_ONLY,
    ALARMS_ONLY,
    TOTAL_SILENCE
}
