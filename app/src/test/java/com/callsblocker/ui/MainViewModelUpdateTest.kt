package com.callsblocker.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.callsblocker.data.BlockedCallLog
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import com.callsblocker.util.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainViewModelUpdateTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: BlockedEntryRepository

    @Mock
    private lateinit var prefsManager: PrefsManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()

        doReturn(flowOf(emptyList<BlockedEntry>())).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        doReturn(flowOf("system")).`when`(prefsManager).appTheme
        doReturn(flowOf("system")).`when`(prefsManager).appLanguage
        
        viewModel = MainViewModel(repository, prefsManager, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateEntry_callsRepositoryUpdate() = runTest {
        val entry = BlockedEntry(
            id = 1,
            pattern = "39333123456",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )

        viewModel.updateEntry(entry)

        advanceUntilIdle()

        verify(repository).update(entry)
    }

    @Test
    fun setAppTheme_callsPrefsManager() = runTest {
        viewModel.setAppTheme("dark")
        advanceUntilIdle()
        verify(prefsManager).setAppTheme("dark")
    }
}
