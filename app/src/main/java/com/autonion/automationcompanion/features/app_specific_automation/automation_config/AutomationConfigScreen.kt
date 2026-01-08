package com.autonion.automationcompanion.features.app_specific_automation.automation_config

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationConfigScreen(
    viewModel: AutomationConfigViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val appName = remember {
        try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(viewModel.packageName ?: "", 0))
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown App"
        }
    }

    val audioSettings by viewModel.audioSettings.collectAsState()
    val displaySettings by viewModel.displaySettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configure: $appName") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AudioSettingsSection(audioSettings, viewModel::onAudioSettingsChanged)
            DisplaySettingsSection(displaySettings, viewModel::onDisplaySettingsChanged)

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    viewModel.saveConfiguration()
                    onSave()
                }) {
                    Text("Save")
                }
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun AudioSettingsSection(settings: AudioSettings, onSettingsChanged: (AudioSettings) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Audio", fontSize = 18.sp, modifier = Modifier.weight(1f))
                Checkbox(checked = settings.isEnabled, onCheckedChange = { 
                    onSettingsChanged(settings.copy(isEnabled = it))
                })
            }
            AnimatedVisibility(visible = isExpanded && settings.isEnabled) {
                Column {
                    Text("Media Volume: ${(settings.mediaVolume ?: 100)}%")
                    Slider(
                        value = (settings.mediaVolume ?: 100) / 100f,
                        onValueChange = { onSettingsChanged(settings.copy(mediaVolume = (it * 100).toInt())) }
                    )
                }
            }
        }
    }
}

@Composable
fun DisplaySettingsSection(settings: DisplaySettings, onSettingsChanged: (DisplaySettings) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Display", fontSize = 18.sp, modifier = Modifier.weight(1f))
                Checkbox(checked = settings.isEnabled, onCheckedChange = { 
                    onSettingsChanged(settings.copy(isEnabled = it))
                })
            }
            AnimatedVisibility(visible = isExpanded && settings.isEnabled) {
                Column {
                    Text("Brightness: ${(settings.brightness ?: 50)}%")
                    Slider(
                        value = (settings.brightness ?: 50) / 100f,
                        onValueChange = { onSettingsChanged(settings.copy(brightness = (it * 100).toInt())) }
                    )
                }
            }
        }
    }
}
