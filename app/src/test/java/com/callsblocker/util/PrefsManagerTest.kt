package com.callsblocker.util

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import android.content.Context

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class PrefsManagerTest {

    private lateinit var context: Context
    private lateinit var prefsManager: PrefsManager

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        prefsManager = PrefsManager(context)
        // DataStore keeps state across tests; reset to default values explicitly
        prefsManager.setOnboardingCompleted(false)
        prefsManager.setNotificationsEnabled(true)
    }

    @Test
    fun onboardingCompleted_defaultFalse() = runTest {
        val value = prefsManager.onboardingCompleted.first()
        assertEquals(false, value)
    }

    @Test
    fun setOnboardingCompleted_savesValue() = runTest {
        prefsManager.setOnboardingCompleted(true)
        val value = prefsManager.onboardingCompleted.first()
        assertEquals(true, value)
    }

    @Test
    fun notificationsEnabled_defaultTrue() = runTest {
        val value = prefsManager.notificationsEnabled.first()
        assertEquals(true, value)
    }

    @Test
    fun setNotificationsEnabled_savesValue() = runTest {
        prefsManager.setNotificationsEnabled(false)
        val value = prefsManager.notificationsEnabled.first()
        assertEquals(false, value)
    }

    @Test
    fun multiplePrefs_independent() = runTest {
        prefsManager.setOnboardingCompleted(true)
        prefsManager.setNotificationsEnabled(false)

        val onboarding = prefsManager.onboardingCompleted.first()
        val notifications = prefsManager.notificationsEnabled.first()

        assertEquals(true, onboarding)
        assertEquals(false, notifications)
    }
}
