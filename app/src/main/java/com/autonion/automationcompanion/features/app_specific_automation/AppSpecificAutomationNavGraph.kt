package com.autonion.automationcompanion.features.app_specific_automation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.autonion.automationcompanion.AccessibilityRouter
import com.autonion.automationcompanion.features.app_specific_automation.add_app.AddAppScreen
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigDatabase
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigRepository
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigScreen
import com.autonion.automationcompanion.features.app_specific_automation.automation_config.AutomationConfigViewModelFactory


object AppSpecificRoutes {
    const val HOME = "home"
    const val ADD_APP = "add_app"
    const val AUTOMATION_CONFIG = "automation_config/{packageName}"

    fun automationConfig(packageName: String) =
        "automation_config/$packageName"
}

@Composable
fun AppSpecificAutomationNavGraph(
    onExitFeature: () -> Unit
) {
    // ✅ REGISTER FEATURE HERE
    DisposableEffect(Unit) {
        AccessibilityRouter.register(
            AppSpecificAutomationFeature
        )
        onDispose {
            AccessibilityRouter.unregister(
                AppSpecificAutomationFeature
            )
        }
    }

    val navController = rememberNavController()
    val context = LocalContext.current

    val db = AutomationConfigDatabase.getDatabase(context)
    val repository = AutomationConfigRepository(db.automationConfigDao())

    val mainViewModelFactory = MainViewModelFactory(repository)
    val configViewModelFactory = AutomationConfigViewModelFactory(repository)


    NavHost(
        navController = navController,
        startDestination = AppSpecificRoutes.HOME
    ) {

        composable(AppSpecificRoutes.HOME) {
            AppSpecificAutomationScreen(
                onBack = onExitFeature,
                onAddApp = {
                    navController.navigate(AppSpecificRoutes.ADD_APP)
                },
                onNavigateToConfig = {
                    navController.navigate(
                        AppSpecificRoutes.automationConfig(it)
                    )
                },
                viewModel = viewModel(factory = mainViewModelFactory)
            )
        }

        composable(AppSpecificRoutes.ADD_APP) {
            AddAppScreen(
                onAppSelected = {
                    navController.navigate(
                        AppSpecificRoutes.automationConfig(it)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppSpecificRoutes.AUTOMATION_CONFIG,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            AutomationConfigScreen(
                viewModel = viewModel(factory = configViewModelFactory),
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
