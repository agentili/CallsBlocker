package com.callsblocker.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val filteredLogs = remember(uiState.systemCallLogs, searchQuery) {
        if (searchQuery.isEmpty()) {
            uiState.systemCallLogs
        } else {
            uiState.systemCallLogs.filter { 
                it.phoneNumber.contains(searchQuery, ignoreCase = true) || 
                it.displayName?.contains(searchQuery, ignoreCase = true) == true
            }
        }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cerca nel registro...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                    } else {
                        Text(stringResource(R.string.permission_read_call_log))
                    }
                },
                navigationIcon = {
                    if (isSearching) {
                        IconButton(onClick = { 
                            isSearching = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Chiudi ricerca")
                        }
                    }
                },
                actions = {
                    if (isSearching) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Cancella")
                            }
                        }
                    } else if (hasPermission && uiState.systemCallLogs.isNotEmpty()) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Cerca")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                if (filteredLogs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (searchQuery.isEmpty()) stringResource(R.string.no_call_log) else "Nessun risultato trovato")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredLogs) { log ->
                        CallLogItem(
                            log = log,
                            onImport = {
                                viewModel.addEntry(
                                    BlockedEntry(
                                        pattern = log.phoneNumber,
                                        isPrefix = false,
                                        label = log.displayName ?: "",
                                        action = CallAction.BLOCK
                                    )
                                )
                            },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Phone Number", log.phoneNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Numero copiato", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider()
                    }
                }
                }
            }
        }
    }
}

@Composable
fun CallLogItem(
    log: CallLogEntry,
    onImport: () -> Unit,
    onCopy: () -> Unit
) {
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
            Row {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copia numero",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onImport) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = stringResource(R.string.import_from_log),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
