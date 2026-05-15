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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import com.callsblocker.R
import com.callsblocker.ui.MainViewModel
import com.callsblocker.ui.components.SegmentedSelector

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
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
                text = stringResource(R.string.protection),
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
                    Text(stringResource(R.string.set_as_screening_app))
                }
            } else {
                Text(
                    text = stringResource(R.string.screening_app_active),
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
                    Text(stringResource(R.string.manage_battery_optimization))
                }
            } else {
                Text(
                    text = stringResource(R.string.battery_not_optimized),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Language Section
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            var expanded by remember { mutableStateOf(false) }
            val languages = listOf(
                "system" to stringResource(R.string.theme_system),
                "en" to "English",
                "es" to "Español",
                "fr" to "Français",
                "de" to "Deutsch",
                "it" to "Italiano"
            )
            val selectedLanguageName = languages.find { it.first == uiState.appLanguage }?.second ?: stringResource(R.string.theme_system)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = selectedLanguageName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                viewModel.setAppLanguage(code)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Notifications Section
            Text(
                text = stringResource(R.string.notifications),
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
                    text = stringResource(R.string.notify_on_block),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { newValue ->
                        scope.launch {
                            prefsManager.setNotificationsEnabled(newValue)
                        }
                    },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        checkedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Theme Selection Section
            Text(
                text = stringResource(R.string.app_theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            val themeOptions = listOf(
                stringResource(R.string.theme_system),
                stringResource(R.string.theme_light),
                stringResource(R.string.theme_dark)
            )
            val currentThemeLabel = when (uiState.appTheme) {
                "light" -> stringResource(R.string.theme_light)
                "dark" -> stringResource(R.string.theme_dark)
                else -> stringResource(R.string.theme_system)
            }

            SegmentedSelector(
                options = themeOptions,
                selectedOption = currentThemeLabel,
                onOptionSelected = { label ->
                    val themeValue = when (label) {
                        themeOptions[1] -> "light"
                        themeOptions[2] -> "dark"
                        else -> "system"
                    }
                    viewModel.setAppTheme(themeValue)
                },
                modifier = Modifier.padding(16.dp)
            )

            // Data Management Section
            Text(
                text = stringResource(R.string.data_management),
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
                Text(stringResource(R.string.remove_all_numbers))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_all_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllEntries()
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete_all), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
