package com.callsblocker.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.callsblocker.ui.components.StatusBanner
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import com.callsblocker.util.PrefsManager
import kotlinx.coroutines.launch

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.callsblocker.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel,
    roleManager: AppRoleManager,
    batteryManager: BatteryOptimizationManager
) {
    val context = LocalContext.current
    val prefsManager = remember { PrefsManager(context) }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val isScreeningActive = uiState.isScreeningActive
    val isBatteryOptimized = uiState.isBatteryOptimized

    val notificationsEnabled by prefsManager.notificationsEnabled.collectAsState(initial = true)
    var notificationsChecked by remember { mutableStateOf(notificationsEnabled) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val roleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.updateScreeningStatus()
    }

    val batteryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.updateScreeningStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Status Banner (shown only if there are issues)
            if (!isScreeningActive || isBatteryOptimized) {
                StatusBanner(
                    isScreeningActive = isScreeningActive,
                    isBatteryOptimized = isBatteryOptimized,
                    onTapConfigure = {
                        (context as? Activity)?.let { activity ->
                            roleManager.requestScreeningRole(activity, roleLauncher)
                        }
                    }
                )
            }

            // Screening Role Section
            Text(
                text = "Protezione",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            if (!isScreeningActive) {
                Button(
                    onClick = {
                        (context as? Activity)?.let { activity ->
                            roleManager.requestScreeningRole(activity, roleLauncher)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Imposta come app di screening")
                }
            } else {
                Text(
                    text = "✓ App impostata come gestore schermata chiamate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Battery Optimization Section
            if (isBatteryOptimized) {
                Button(
                    onClick = {
                        try {
                            batteryLauncher.launch(batteryManager.buildExemptionIntent())
                        } catch (_: Exception) {
                            (context as? Activity)?.let { activity ->
                                batteryManager.requestBatteryOptimizationExemption(activity)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Gestisci ottimizzazione batteria")
                }
            } else {
                Text(
                    text = "✓ Batteria non ottimizzata — il servizio non verrà killato",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Notifications Section
            Text(
                text = "Notifiche",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifica quando una chiamata viene bloccata",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = notificationsChecked,
                    onCheckedChange = { newValue ->
                        notificationsChecked = newValue
                        scope.launch {
                            prefsManager.setNotificationsEnabled(newValue)
                        }
                    }
                )
            }

            // Data Management Section
            Text(
                text = "Gestione Dati",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            Button(
                onClick = { showDeleteDialog = true },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Rimuovi tutti i numeri")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler rimuovere tutti i numeri dalla blacklist? Questa azione non può essere annullata.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllEntries()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Elimina tutto", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}
