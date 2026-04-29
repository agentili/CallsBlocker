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
    val isScreeningActive: Boolean = false,
    val isBatteryOptimized: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: BlockedEntryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeEntries()
        observeLogs()
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

    fun updateEntry(entry: BlockedEntry) {
        viewModelScope.launch {
            repository.update(entry)
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
