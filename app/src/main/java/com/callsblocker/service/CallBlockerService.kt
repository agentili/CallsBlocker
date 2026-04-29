package com.callsblocker.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.callsblocker.data.BlockedCallLog
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import com.callsblocker.util.NotificationHelper
import com.callsblocker.util.NumberMatcher
import com.callsblocker.util.PrefsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CallBlockerService : CallScreeningService() {

    @Inject
    lateinit var repository: BlockedEntryRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var prefsManager: PrefsManager

    override fun onScreenCall(callDetails: Call.Details) {
        if (!::repository.isInitialized) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val incomingNumber = callDetails.handle?.schemeSpecificPart ?: ""
        if (incomingNumber.isEmpty()) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val entries = try {
            runBlocking { repository.getAllSync() }
        } catch (e: Exception) {
            emptyList()
        }

        val match = entries.firstOrNull { NumberMatcher.matches(incomingNumber, it) }

        // Log the call if it matches a blacklist entry
        match?.let {
            runBlocking {
                repository.insertLog(
                    BlockedCallLog(
                        number = incomingNumber,
                        timestamp = System.currentTimeMillis(),
                        action = it.action,
                        label = it.label
                    )
                )

                // Notify if enabled
                val notificationsEnabled = try {
                    prefsManager.notificationsEnabled.first()
                } catch (e: Exception) {
                    true
                }

                if (notificationsEnabled) {
                    notificationHelper.showBlockedNotification(incomingNumber, it.action, it.label)
                }
            }
        }

        val response = when (match?.action) {
            CallAction.BLOCK -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .build()

            CallAction.SILENCE -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(false)
                .build()

            else -> CallResponse.Builder().build()
        }

        respondToCall(callDetails, response)
    }
}
