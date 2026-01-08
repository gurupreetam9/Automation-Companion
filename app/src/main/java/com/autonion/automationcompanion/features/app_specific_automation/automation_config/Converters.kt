package com.autonion.automationcompanion.features.app_specific_automation.automation_config
import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAudioSettings(audioSettings: AudioSettings): String {
        return gson.toJson(audioSettings)
    }

    @TypeConverter
    fun toAudioSettings(audioSettingsString: String): AudioSettings {
        return gson.fromJson(audioSettingsString, AudioSettings::class.java)
    }

    @TypeConverter
    fun fromDisplaySettings(displaySettings: DisplaySettings): String {
        return gson.toJson(displaySettings)
    }

    @TypeConverter
    fun toDisplaySettings(displaySettingsString: String): DisplaySettings {
        return gson.fromJson(displaySettingsString, DisplaySettings::class.java)
    }
}
