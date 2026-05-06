package com.callsblocker.util

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.callsblocker.data.CallAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class NotificationHelperTest {

    private lateinit var context: Context
    private lateinit var helper: NotificationHelper
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        helper = NotificationHelper(context)
    }

    @Test
    fun showBlockedNotification_createsNotification() {
        helper.showBlockedNotification("333123456", CallAction.BLOCK, "Spam")
        
        val shadowManager = shadowOf(notificationManager)
        assert(shadowManager.allNotifications.size == 1)
    }
    
    @Test
    fun showBlockedNotification_allowAction_doesNothing() {
        helper.showBlockedNotification("333123456", CallAction.ALLOW, "Friend")
        val shadowManager = shadowOf(notificationManager)
        assert(shadowManager.allNotifications.isEmpty())
    }
}
