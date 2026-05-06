package com.callsblocker.service

import android.net.Uri
import android.telecom.Call
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import com.callsblocker.util.NotificationHelper
import com.callsblocker.util.PrefsManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class CallBlockerServiceTest {

    private lateinit var service: CallBlockerService
    private val repository = mockk<BlockedEntryRepository>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>(relaxed = true)
    private val prefsManager = mockk<PrefsManager>(relaxed = true)

    @Before
    fun setup() {
        service = CallBlockerService()
        service.repository = repository
        service.notificationHelper = notificationHelper
        service.prefsManager = prefsManager
    }

    @Test
    fun onScreenCall_noMatch_allowsCall() {
        val details = mockk<Call.Details>()
        every { details.handle } returns Uri.parse("tel:123456")
        coEvery { repository.getAllSync() } returns emptyList()

        service.onScreenCall(details)
        
        coVerify { repository.getAllSync() }
    }

    @Test
    fun onScreenCall_blockMatch_logsAndNotifies() {
        val details = mockk<Call.Details>()
        every { details.handle } returns Uri.parse("tel:333123")
        val entry = BlockedEntry(pattern = "333", isPrefix = true, label = "Spam", action = CallAction.BLOCK)
        coEvery { repository.getAllSync() } returns listOf(entry)
        every { prefsManager.notificationsEnabled } returns flowOf(true)

        service.onScreenCall(details)

        coVerify { repository.insertLog(any()) }
        io.mockk.verify { notificationHelper.showBlockedNotification("333123", CallAction.BLOCK, "Spam") }
    }
    
    @Test
    fun onScreenCall_emptyNumber_returnsEarly() {
        val details = mockk<Call.Details>()
        every { details.handle } returns null
        
        service.onScreenCall(details)
        
        coVerify(exactly = 0) { repository.getAllSync() }
    }
}
