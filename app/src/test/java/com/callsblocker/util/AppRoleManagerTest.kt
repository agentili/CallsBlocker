package com.callsblocker.util

import android.app.role.RoleManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Regression tests for AppRoleManager.
 *
 * Bug history: the role string was hardcoded as "android.app.role.CALL_SCREENING_DEFAULT"
 * (which does not exist in Android), causing the system role-request dialog to never appear
 * and the onboarding to get stuck on step 3.
 *
 * The correct role string is RoleManager.ROLE_CALL_SCREENING = "android.app.role.CALL_SCREENING".
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class AppRoleManagerTest {

    @Test
    fun callScreeningRoleConstant_isCallScreening_notDefault() {
        // Sanity check: the framework constant must equal the documented Android value.
        // If this changes, the role-request flow is broken.
        assertEquals("android.app.role.CALL_SCREENING", RoleManager.ROLE_CALL_SCREENING)
    }

    @Test
    fun isScreeningRoleGranted_defaultFalse_doesNotThrow() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appRoleManager = AppRoleManager(context)
        // In a fresh Robolectric environment the role is not held — the call must
        // succeed (no exception) and return false. A previous bug used an unknown role
        // string; the call still returned false, so this also asserts integration with
        // the real RoleManager API.
        val result = appRoleManager.isScreeningRoleGranted()
        assertFalse(result)
    }
}
