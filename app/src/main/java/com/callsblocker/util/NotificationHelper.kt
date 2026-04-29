package com.callsblocker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.callsblocker.R
import com.callsblocker.data.CallAction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "call_blocking_channel"
        private const val CHANNEL_NAME = "Notifiche Chiamate Bloccate"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBlockedNotification(number: String, action: CallAction, label: String?) {
        val actionStr = when (action) {
            CallAction.BLOCK -> "Bloccata chiamata da"
            CallAction.SILENCE -> "Silenziata chiamata da"
            else -> return
        }

        val title = "$actionStr $number"
        val text = if (!label.isNullOrEmpty()) "Identificato come: $label" else "Numero presente in blacklist"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_def_app_icon) // Using system icon for now
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(number.hashCode(), builder.build())
    }
}
