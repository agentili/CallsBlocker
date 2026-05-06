package com.callsblocker.util

import android.content.Context
import android.os.PowerManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class BatteryOptimizationManagerTest {

    private lateinit var context: Context
    private lateinit var manager: BatteryOptimizationManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = BatteryOptimizationManager(context)
    }

    @Test
    fun isBatteryOptimizationActive_returnsCorrectValue() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val shadowPowerManager = shadowOf(powerManager)
        
        // When NOT ignoring battery optimizations, it is "active"
        shadowPowerManager.setIgnoringBatteryOptimizations(context.packageName, false)
        assert(manager.isBatteryOptimizationActive())

        // When ignoring, it is NOT "active"
        shadowPowerManager.setIgnoringBatteryOptimizations(context.packageName, true)
        assert(!manager.isBatteryOptimizationActive())
    }

    @Test
    fun buildExemptionIntent_isCorrect() {
        val intent = manager.buildExemptionIntent()
        assertNotNull(intent)
        assert(intent.action == android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        assert(intent.data?.toString() == "package:${context.packageName}")
    }
}
