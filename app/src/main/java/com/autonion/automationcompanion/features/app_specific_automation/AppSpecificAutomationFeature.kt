package com.autonion.automationcompanion.features.app_specific_automation

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.autonion.automationcompanion.AccessibilityFeature
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigDatabase
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

object AppSpecificAutomationFeature : AccessibilityFeature {

    private const val TAG = "AppSpecificAutomation"

    private var service: AccessibilityService? = null
    private lateinit var repository: AutomationConfigRepository
    private val scope = CoroutineScope(Dispatchers.IO)

    // Audio
    private var originalMediaVolume: Int = -1
    private lateinit var audioManager: AudioManager

    // Display
    private var originalBrightness: Int = -1
    private var originalBrightnessMode: Int = -1

    override fun onServiceConnected(service: AccessibilityService) {
        this.service = service
        Log.d(TAG, "Feature connected to AccessibilityService")

        audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        scope.launch {
            val db = AutomationConfigDatabase.getDatabase(service)
            repository = AutomationConfigRepository(db.automationConfigDao())
            storeOriginalSettings(service)
        }
    }

    override fun onEvent(
        service: AccessibilityService,
        event: AccessibilityEvent
    ) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (!::repository.isInitialized) return

        val packageName = event.packageName?.toString() ?: return

        scope.launch {
            try {
                val config = repository
                    .getAutomationConfig(packageName)
                    .firstOrNull()

                if (config != null) {
                    if (config.audioSettings.isEnabled) {
                        applyAudioSettings(config.audioSettings)
                    } else {
                        revertAudioSettings()
                    }

                    if (config.displaySettings.isEnabled) {
                        applyDisplaySettings(service, config.displaySettings)
                    } else {
                        revertDisplaySettings(service)
                    }
                } else {
                    revertAudioSettings()
                    revertDisplaySettings(service)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing automation for $packageName", e)
            }
        }
    }

    // ---------------- AUDIO ----------------

    private fun applyAudioSettings(settings: com.autonion.automationcompanion.features.app_specific_automation.automation_config.AudioSettings) {
        try {
            settings.mediaVolume?.let { percent ->
                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val newVolume = (max * (percent / 100f)).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply audio settings", e)
        }
    }

    private fun revertAudioSettings() {
        if (originalMediaVolume != -1) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                originalMediaVolume,
                0
            )
        }
    }

    // ---------------- DISPLAY ----------------

    private fun applyDisplaySettings(
        service: AccessibilityService,
        settings: com.autonion.automationcompanion.features.app_specific_automation.automation_config.DisplaySettings
    ) {
        if (!Settings.System.canWrite(service)) {
            requestWriteSettings(service)
            return
        }

        try {
            Settings.System.putInt(
                service.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )

            settings.brightness?.let {
                val value = (it / 100f * 255).toInt().coerceIn(0, 255)
                Settings.System.putInt(
                    service.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    value
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply display settings", e)
        }
    }

    private fun revertDisplaySettings(service: AccessibilityService) {
        if (!Settings.System.canWrite(service)) {
            Log.d(TAG, "WRITE_SETTINGS not granted, skipping revert")
            return
        }

        if (originalBrightness != -1 && originalBrightnessMode != -1) {
            Settings.System.putInt(
                service.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                originalBrightness
            )
            Settings.System.putInt(
                service.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                originalBrightnessMode
            )
        }
    }

    // ---------------- HELPERS ----------------

    private fun storeOriginalSettings(service: AccessibilityService) {
        originalMediaVolume =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        try {
            originalBrightness = Settings.System.getInt(
                service.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            originalBrightnessMode = Settings.System.getInt(
                service.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
        } catch (_: Exception) {
            originalBrightness = 128
            originalBrightnessMode =
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }
    }

    private fun requestWriteSettings(service: AccessibilityService) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${service.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        service.startActivity(intent)
    }
}
