package com.callsblocker.ui.screens

import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class SettingsScreenSignatureTest {

    @Test
    fun settingsScreen_acceptsRoleManager() {
        val method = Class.forName("com.callsblocker.ui.screens.SettingsScreenKt")
            .methods
            .first { it.name == "SettingsScreen" }

        val paramTypes = method.parameterTypes.toList()
        assert(paramTypes.any { it == AppRoleManager::class.java }) {
            "SettingsScreen must accept AppRoleManager parameter, found types: $paramTypes"
        }
    }

    @Test
    fun settingsScreen_acceptsBatteryManager() {
        val method = Class.forName("com.callsblocker.ui.screens.SettingsScreenKt")
            .methods
            .first { it.name == "SettingsScreen" }

        val paramTypes = method.parameterTypes.toList()
        assert(paramTypes.any { it == BatteryOptimizationManager::class.java }) {
            "SettingsScreen must accept BatteryOptimizationManager parameter, found types: $paramTypes"
        }
    }
}
