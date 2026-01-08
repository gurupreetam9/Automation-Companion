package com.autonion.automationcompanion.features.app_specific_automation.add_app

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable?
)