package com.callsblocker.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BatteryOptimizationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun isBatteryOptimizationActive(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            powerManager?.isIgnoringBatteryOptimizations(context.packageName)?.not() ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun requestBatteryOptimizationExemption(activity: Activity) {
        try {
            activity.startActivity(buildExemptionIntent())
        } catch (e: Exception) {
            // If the device doesn't support this, open battery settings
            try {
                activity.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun buildExemptionIntent(): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = android.net.Uri.parse("package:${context.packageName}")
        }
}
