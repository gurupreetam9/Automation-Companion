package com.autonion.automationcompanion.features.app_specific_automation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autonion.automationcompanion.features.gesture_recording_playback.overlay.AutomationService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSpecificAutomationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAddApp: () -> Unit,
    onNavigateToConfig: (String) -> Unit
)
 {
    val viewModel: MainViewModel = viewModel()
    val automationConfigs by viewModel.automationConfigs.collectAsState()
    val context = LocalContext.current

    var isAccessibilityEnabled by remember {
        mutableStateOf(isAccessibilityServiceEnabled(context))
    }

    var canWriteSettings by remember {
        mutableStateOf(Settings.System.canWrite(context))
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityEnabled = isAccessibilityServiceEnabled(context)
                canWriteSettings = Settings.System.canWrite(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App-Specific Automation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddApp) {
                Icon(Icons.Default.Add, contentDescription = "Add App")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                !isAccessibilityEnabled -> {
                    PermissionPrompt(
                        text = "Accessibility Service is not enabled.",
                        buttonText = "Enable",
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            )
                        }
                    )
                }

                !canWriteSettings -> {
                    PermissionPrompt(
                        text = "Write Settings permission not granted.",
                        buttonText = "Grant",
                        onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                automationConfigs.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No apps configured yet")
                    }
                }

                else -> {
                    LazyColumn {
                        items(automationConfigs) { config ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToConfig(config.packageName)
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(config.packageName)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionPrompt(
    text: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onClick) {
            Text(buttonText)
        }
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedComponent = ComponentName(context, AutomationService::class.java)
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val splitter = TextUtils.SimpleStringSplitter(':')
    splitter.setString(enabledServices)

    while (splitter.hasNext()) {
        if (expectedComponent.flattenToString()
                .equals(splitter.next(), ignoreCase = true)
        ) return true
    }
    return false
}
