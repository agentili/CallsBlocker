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

        // Check system spam identification (API 29+) using reflection to avoid classpath issues
        var isSystemSpam = false
        var callIdName: String? = null
        var callIdDescription: String? = null
        
        try {
            val getCallIdMethod = callDetails.javaClass.getMethod("getCallIdentification")
            val callId = getCallIdMethod.invoke(callDetails)
            if (callId != null) {
                val getNuisanceMethod = callId.javaClass.getMethod("getNuisanceConfidence")
                val getNameMethod = callId.javaClass.getMethod("getName")
                val getDescriptionMethod = callId.javaClass.getMethod("getDescription")
                
                val confidence = getNuisanceMethod.invoke(callId) as Int
                callIdName = getNameMethod.invoke(callId) as? String
                callIdDescription = getDescriptionMethod.invoke(callId) as? String
                
                isSystemSpam = confidence >= 2 || // 2 = MEDIUM
                        callIdName?.contains("spam", ignoreCase = true) == true ||
                        callIdDescription?.contains("spam", ignoreCase = true) == true
            }
        } catch (e: Exception) {
            // Fallback or ignore if not supported
        }

        val entries = try {
            runBlocking { repository.getAllSync() }
        } catch (e: Exception) {
            emptyList()
        }

        val match = entries.firstOrNull { NumberMatcher.matches(incomingNumber, it) }

        // Determine action and logging
        var logEntry: BlockedCallLog? = null

        if (match != null) {
            logEntry = BlockedCallLog(
                number = incomingNumber,
                timestamp = System.currentTimeMillis(),
                action = match.action,
                label = match.label,
                isSpam = isSystemSpam
            )
        } else if (isSystemSpam) {
            // Auto-log system identified spam even if not in blacklist
            logEntry = BlockedCallLog(
                number = incomingNumber,
                timestamp = System.currentTimeMillis(),
                action = CallAction.ALLOW, // We don't block automatically yet, just log
                label = callIdName ?: callIdDescription ?: "Sospetto Spam Sistema",
                isSpam = true,
                isAutoAdded = true
            )
        }

        // Persist log and notify
        logEntry?.let { log ->
            runBlocking {
                repository.insertLog(log)

                val notificationsEnabled = try {
                    prefsManager.notificationsEnabled.first()
                } catch (e: Exception) {
                    true
                }

                if (notificationsEnabled) {
                    val label = log.label ?: if (log.isSpam) "Sospetto Spam" else ""
                    notificationHelper.showBlockedNotification(incomingNumber, log.action, label)
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
