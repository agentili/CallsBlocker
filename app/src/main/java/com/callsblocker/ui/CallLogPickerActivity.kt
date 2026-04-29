package com.callsblocker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CallLog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callsblocker.ui.theme.CallsBlockerTheme
import dagger.hilt.android.AndroidEntryPoint

data class CallLogEntry(
    val phoneNumber: String,
    val displayName: String?,
    val duration: Long,
    val date: Long
)

@AndroidEntryPoint
class CallLogPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val callLogs = loadRecentCalls()

        setContent {
            CallsBlockerTheme {
                CallLogPickerScreen(
                    callLogs = callLogs,
                    onSelectNumber = { phoneNumber ->
                        val intent = Intent()
                        intent.putExtra("selected_number", phoneNumber)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    },
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    private fun loadRecentCalls(): List<CallLogEntry> {
        val calls = mutableListOf<CallLogEntry>()
        try {
            val cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.DATE
                ),
                null,
                null,
                "${CallLog.Calls.DATE} DESC LIMIT 50"
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val number = it.getString(0)
                    val name = it.getString(1)
                    val duration = it.getLong(2)
                    val date = it.getLong(3)

                    if (number != null) {
                        calls.add(CallLogEntry(number, name, duration, date))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calls
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallLogPickerScreen(
    callLogs: List<CallLogEntry>,
    onSelectNumber: (String) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seleziona da registro chiamate") })
        }
    ) { paddingValues ->
        if (callLogs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nessuna chiamata nel registro",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(callLogs) { callLogEntry ->
                    CallLogItem(
                        entry = callLogEntry,
                        onClick = { onSelectNumber(callLogEntry.phoneNumber) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CallLogItem(
    entry: CallLogEntry,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = entry.phoneNumber,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!entry.displayName.isNullOrEmpty()) {
            Text(
                text = entry.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(entry.date),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
