package com.callsblocker.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.callsblocker.R
import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import com.callsblocker.ui.CallLogEntry
import com.callsblocker.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallLogScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.fetchSystemCallLogs()
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.fetchSystemCallLogs()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!hasPermission) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.permission_denied_log))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.READ_CALL_LOG) }) {
                        Text(stringResource(R.string.request_permission))
                    }
                }
            }
        } else {
            if (uiState.systemCallLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_call_log))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.systemCallLogs) { log ->
                        CallLogItem(log) {
                            viewModel.addEntry(
                                BlockedEntry(
                                    pattern = log.phoneNumber,
                                    isPrefix = false,
                                    label = log.displayName ?: "",
                                    action = CallAction.BLOCK
                                )
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun CallLogItem(log: CallLogEntry, onImport: () -> Unit) {
    val sdf = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    
    ListItem(
        headlineContent = { 
            Text(
                text = log.displayName ?: log.phoneNumber,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Column {
                if (log.displayName != null) Text(log.phoneNumber)
                Text(sdf.format(Date(log.date)))
            }
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            IconButton(onClick = onImport) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = stringResource(R.string.import_from_log),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}
