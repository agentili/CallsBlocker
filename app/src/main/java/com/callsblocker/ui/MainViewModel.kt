package com.callsblocker.ui

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callsblocker.data.BlockedCallLog
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.BlockedEntryRepository
import com.callsblocker.util.CsvManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

data class UiState(
    val entries: List<BlockedEntry> = emptyList(),
    val callLogs: List<BlockedCallLog> = emptyList(),
    val systemCallLogs: List<CallLogEntry> = emptyList(),
    val isScreeningActive: Boolean = false,
    val isBatteryOptimized: Boolean = false,
    val userMessage: String? = null,
    val appTheme: String = "system",
    val appLanguage: String = "system"
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: BlockedEntryRepository,
    private val prefsManager: com.callsblocker.util.PrefsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeEntries()
        observeLogs()
        observeTheme()
        observeLanguage()
        updateScreeningStatus()
    }

    private fun observeEntries() {
        viewModelScope.launch {
            repository.getAll().collect { entries ->
                _uiState.value = _uiState.value.copy(entries = entries)
            }
        }
    }

    private fun observeLogs() {
        viewModelScope.launch {
            repository.getAllLogs().collect { logs ->
                _uiState.value = _uiState.value.copy(callLogs = logs)
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            prefsManager.appTheme.collect { theme ->
                _uiState.value = _uiState.value.copy(appTheme = theme)
            }
        }
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            prefsManager.appLanguage.collect { language ->
                _uiState.value = _uiState.value.copy(appLanguage = language)
            }
        }
    }

    fun updateScreeningStatus() {
        viewModelScope.launch {
            val isActive = isCallScreeningRoleGranted()
            val isBatteryOptimized = isBatteryOptimizationActive()
            _uiState.value = _uiState.value.copy(
                isScreeningActive = isActive,
                isBatteryOptimized = isBatteryOptimized
            )
        }
    }

    private fun isCallScreeningRoleGranted(): Boolean {
        return try {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? android.app.role.RoleManager
            roleManager?.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING) ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun isBatteryOptimizationActive(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
            powerManager?.isIgnoringBatteryOptimizations(context.packageName)?.not() ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun addEntry(entry: BlockedEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            repository.deleteById(entryId)
        }
    }

    fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun clearCallLogs() {
        viewModelScope.launch {
            repository.deleteAllLogs()
        }
    }

    fun deleteCallLog(logId: Long) {
        viewModelScope.launch {
            repository.deleteLog(logId)
        }
    }

    fun clearUserMessage() {
        _uiState.value = _uiState.value.copy(userMessage = null)
    }

    fun fetchSystemCallLogs() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val calls = mutableListOf<CallLogEntry>()
            try {
                val projection = arrayOf(
                    android.provider.CallLog.Calls.NUMBER,
                    android.provider.CallLog.Calls.CACHED_NAME,
                    android.provider.CallLog.Calls.DURATION,
                    android.provider.CallLog.Calls.DATE
                )

                // Some devices don't support LIMIT in the sort order string and return empty/fail.
                // We'll fetch without LIMIT and take the first 100 manually.
                val cursor = context.contentResolver.query(
                    android.provider.CallLog.Calls.CONTENT_URI,
                    projection,
                    null,
                    null,
                    "${android.provider.CallLog.Calls.DATE} DESC"
                )

                cursor?.use {
                    val numberIdx = it.getColumnIndex(android.provider.CallLog.Calls.NUMBER)
                    val nameIdx = it.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)
                    val durationIdx = it.getColumnIndex(android.provider.CallLog.Calls.DURATION)
                    val dateIdx = it.getColumnIndex(android.provider.CallLog.Calls.DATE)

                    var count = 0
                    while (it.moveToNext() && count < 100) {
                        val number = if (numberIdx != -1) it.getString(numberIdx) else null
                        val name = if (nameIdx != -1) it.getString(nameIdx) else null
                        val duration = if (durationIdx != -1) it.getLong(durationIdx) else 0L
                        val date = if (dateIdx != -1) it.getLong(dateIdx) else 0L

                        if (!number.isNullOrEmpty()) {
                            calls.add(CallLogEntry(number, name, duration, date))
                            count++
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error fetching call logs", e)
            }
            _uiState.value = _uiState.value.copy(systemCallLogs = calls)
        }
    }

    fun updateEntry(entry: BlockedEntry) {
        viewModelScope.launch {
            repository.update(entry)
        }
    }

    fun setAppTheme(theme: String) {
        viewModelScope.launch {
            prefsManager.setAppTheme(theme)
        }
    }

    fun setAppLanguage(language: String) {
        viewModelScope.launch {
            prefsManager.setAppLanguage(language)
        }
    }

    fun exportCsv(outputStream: OutputStream) {
        viewModelScope.launch {
            try {
                CsvManager.export(_uiState.value.entries, outputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importCsv(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                val importedEntries = CsvManager.import(inputStream)
                if (importedEntries.isNotEmpty()) {
                    repository.insertAll(importedEntries)
                    _uiState.value = _uiState.value.copy(
                        userMessage = "Importati ${importedEntries.size} numeri"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        userMessage = "Nessun numero trovato nel file"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error importing entries", e)
                _uiState.value = _uiState.value.copy(
                    userMessage = "Errore durante l'importazione"
                )
            }
        }
    }
}
