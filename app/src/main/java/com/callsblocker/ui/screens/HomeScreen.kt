package com.callsblocker.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.callsblocker.R
import com.callsblocker.data.BlockedEntry
import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.components.BlockedEntryItem
import com.callsblocker.ui.components.EntryBottomSheet
import com.callsblocker.ui.components.StatusBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var showEntrySheet by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<BlockedEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<BlockedEntry?>(null) }

    val filteredEntries = remember(uiState.entries, searchQuery) {
        if (searchQuery.isEmpty()) {
            uiState.entries
        } else {
            uiState.entries.filter { 
                it.pattern.contains(searchQuery, ignoreCase = true) || 
                it.label.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                viewModel.importCsv(inputStream)
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                viewModel.exportCsv(outputStream)
            }
        }
    }

    // Refresh screening status when HomeScreen resumes (after returning from SettingsScreen)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.updateScreeningStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
                            placeholder = { Text("Cerca numero o nome...") },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("CallsBlocker")
                            if (uiState.isScreeningActive && !uiState.isBatteryOptimized) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Attivo",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (isSearching) {
                        IconButton(onClick = { 
                            isSearching = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Chiudi ricerca")
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
                    } else {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Cerca")
                        }
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Chiamate Intercettate") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("logs")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Impostazioni") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("settings")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Importa CSV/TXT") },
                                onClick = {
                                    menuExpanded = false
                                    importLauncher.launch(arrayOf("text/csv", "text/plain", "*/*"))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Esporta CSV/TXT") },
                                onClick = {
                                    menuExpanded = false
                                    exportLauncher.launch("callsblocker_export.csv")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Info") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("info")
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    entryToEdit = null
                    showEntrySheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp) // Adjust padding to sit just above bottom bar
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (!uiState.isScreeningActive || uiState.isBatteryOptimized) {
                    item {
                        StatusBanner(
                            isScreeningActive = uiState.isScreeningActive,
                            isBatteryOptimized = uiState.isBatteryOptimized,
                            onTapConfigure = { navController.navigate("settings") }
                        )
                    }
                }

                if (filteredEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "Nessuna voce nella blacklist" else "Nessun risultato trovato",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredEntries) { entry ->
                        BlockedEntryItem(
                            entry = entry,
                            onDelete = { entryToDelete = entry },
                            onEdit = { 
                                entryToEdit = it
                                showEntrySheet = true
                            },
                            onActionChange = { updatedEntry, newAction ->
                                viewModel.updateEntry(updatedEntry.copy(action = newAction))
                            }
                        )
                    }
                }
                
                // Add extra space at the bottom to ensure content isn't covered by FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        EntryBottomSheet(
            showSheet = showEntrySheet,
            entryToEdit = entryToEdit,
            onDismiss = { showEntrySheet = false },
            onConfirm = { entry ->
                if (entry.id == 0L) {
                    viewModel.addEntry(entry)
                } else {
                    viewModel.updateEntry(entry)
                }
            }
        )

        if (entryToDelete != null) {
            AlertDialog(
                onDismissRequest = { entryToDelete = null },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = { Text(stringResource(R.string.delete_confirmation_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            entryToDelete?.let { viewModel.deleteEntry(it.id) }
                            entryToDelete = null
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { entryToDelete = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
