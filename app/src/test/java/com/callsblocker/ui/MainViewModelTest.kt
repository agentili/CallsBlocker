package com.callsblocker.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.callsblocker.data.BlockedCallLog
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.data.CallAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: BlockedEntryRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()

        doReturn(flowOf(emptyList<BlockedEntry>())).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        
        viewModel = MainViewModel(repository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyEmpty() = runTest {
        val state = viewModel.uiState.value
        assertEquals(emptyList<BlockedEntry>(), state.entries)
    }

    @Test
    fun uiState_entriesEmitted() = runTest {
        val entry = BlockedEntry(
            id = 1,
            pattern = "+39333",
            isPrefix = true,
            label = "Test",
            action = CallAction.BLOCK
        )
        doReturn(flowOf(listOf(entry))).`when`(repository).getAll()

        val newViewModel = MainViewModel(repository, context)
        val state = newViewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals(entry, state.entries.first())
    }

    @Test
    fun isScreeningActive_returnsCorrectStatus() = runTest {
        viewModel.updateScreeningStatus()
        val state = viewModel.uiState.value
        assertEquals(false, state.isScreeningActive)
    }

    @Test
    fun deleteEntry_callsDeleteById_notDeleteWithEntity() = runTest {
        // Arrange: una entry nella lista
        val entry = BlockedEntry(id = 42L, pattern = "333", isPrefix = false, label = "", action = CallAction.BLOCK)
        doReturn(flowOf(listOf(entry))).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        val vm = MainViewModel(repository, context)

        // Act
        vm.deleteEntry(42L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: deleteById chiamato, non delete(entity)
        org.mockito.kotlin.verify(repository).deleteById(42L)
        org.mockito.kotlin.verify(repository, org.mockito.Mockito.never()).delete(entry)
    }

    @Test
    fun deleteCallLog_callsDeleteLog() = runTest {
        doReturn(flowOf(emptyList<BlockedEntry>())).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        val vm = MainViewModel(repository, context)

        vm.deleteCallLog(99L)
        testDispatcher.scheduler.advanceUntilIdle()

        org.mockito.kotlin.verify(repository).deleteLog(99L)
    }

    @Test
    fun importCsv_success_setsUserMessage() = runTest {
        doReturn(flowOf(emptyList<BlockedEntry>())).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        val vm = MainViewModel(repository, context)

        val csv = "pattern,isPrefix,label,action\n+39333123456,false,Test,BLOCK\n".toByteArray()
        vm.importCsv(java.io.ByteArrayInputStream(csv))
        testDispatcher.scheduler.advanceUntilIdle()

        val message = vm.uiState.value.userMessage
        assert(message != null && message.contains("1")) {
            "userMessage should mention the count of imported entries, was: $message"
        }
    }

    @Test
    fun clearUserMessage_clearsMessage() = runTest {
        doReturn(flowOf(emptyList<BlockedEntry>())).`when`(repository).getAll()
        doReturn(flowOf(emptyList<BlockedCallLog>())).`when`(repository).getAllLogs()
        val vm = MainViewModel(repository, context)

        val csv = "pattern,isPrefix,label,action\n+39333,false,Test,BLOCK\n".toByteArray()
        vm.importCsv(java.io.ByteArrayInputStream(csv))
        testDispatcher.scheduler.advanceUntilIdle()
        assert(vm.uiState.value.userMessage != null)

        vm.clearUserMessage()

        assertEquals(null, vm.uiState.value.userMessage)
    }
}
