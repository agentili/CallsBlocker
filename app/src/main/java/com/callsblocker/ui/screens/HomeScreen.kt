package com.callsblocker.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.callsblocker.ui.MainViewModel
import com.callsblocker.data.BlockedEntry
import com.callsblocker.ui.CallLogPickerActivity
import com.callsblocker.ui.components.EntryBottomSheet
import com.callsblocker.ui.components.BlockedEntryItem
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("CallsBlocker")
                        if (uiState.isScreeningActive && !uiState.isBatteryOptimized) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Attivo",
                                tint = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                modifier = androidx.compose.ui.Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                },
                actions = {
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
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    entryToEdit = null
                    showEntrySheet = true 
                },
                containerColor = MaterialTheme.colorScheme.primary
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

                if (uiState.entries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nessuna voce nella blacklist",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(uiState.entries) { entry ->
                        BlockedEntryItem(
                            entry = entry,
                            onDelete = { viewModel.deleteEntry(it) },
                            onEdit = { 
                                entryToEdit = it
                                showEntrySheet = true
                            }
                        )
                    }
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
    }
}
