package com.callsblocker.util

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppRoleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun isScreeningRoleGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    fun requestScreeningRole(
        activity: Activity,
        launcher: ActivityResultLauncher<Intent>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                roleManager?.let {
                    if (!it.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) return
                    if (it.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) return
                    val intent = it.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    launcher.launch(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
