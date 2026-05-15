package com.callsblocker.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.callsblocker.R
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: AppRoleManager

    @Inject
    lateinit var batteryManager: BatteryOptimizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CallsBlockerTheme {
                val viewModel = hiltViewModel<OnboardingViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                // Apply language preference (in case it was set before)
                val appLanguage by viewModel.appLanguage.collectAsState(initial = "system")
                LaunchedEffect(appLanguage) {
                    val appLocale: LocaleListCompat = if (appLanguage == "system") {
                        LocaleListCompat.getEmptyLocaleList()
                    } else {
                        LocaleListCompat.forLanguageTags(appLanguage)
                    }
                    if (AppCompatDelegate.getApplicationLocales() != appLocale) {
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                }

                OnboardingContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    roleManager = roleManager,
                    batteryManager = batteryManager,
                    onCompleted = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    viewModel: OnboardingViewModel,
    roleManager: AppRoleManager,
    batteryManager: BatteryOptimizationManager,
    onCompleted: () -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Proceed to next step regardless
        viewModel.nextStep()
    }

    val roleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Proceed to next step after role request
        viewModel.nextStep()
    }

    val batteryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Proceed to next step after battery dialog returns
        viewModel.nextStep()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState.currentStep) {
                1 -> Step1Welcome(viewModel = viewModel)
                2 -> Step2ReadCallLog(viewModel = viewModel, permissionLauncher = permissionLauncher)
                3 -> Step3ScreeningRole(viewModel = viewModel, roleManager = roleManager, roleLauncher = roleLauncher)
                4 -> Step4BatteryOptimization(viewModel = viewModel, batteryManager = batteryManager, batteryLauncher = batteryLauncher)
                5 -> Step5Features(viewModel = viewModel)
                6 -> Step6Completed(viewModel = viewModel)
            }
        }
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onCompleted()
        }
    }
}

@Composable
private fun Step1Welcome(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.welcome_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
private fun Step2ReadCallLog(
    viewModel: OnboardingViewModel,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.permission_read_call_log),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.permission_read_call_log_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.grant_access))
        }
    }
}

@Composable
private fun Step3ScreeningRole(
    viewModel: OnboardingViewModel,
    roleManager: AppRoleManager,
    roleLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.set_as_screening_app),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.screening_role_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = {
                (context as? android.app.Activity)?.let { activity ->
                    roleManager.requestScreeningRole(activity, roleLauncher)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.set_as_manager))
        }
    }
}

@Composable
private fun Step4BatteryOptimization(
    viewModel: OnboardingViewModel,
    batteryManager: BatteryOptimizationManager,
    batteryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.battery_exemption_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.battery_exemption_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = {
                try {
                    batteryLauncher.launch(batteryManager.buildExemptionIntent())
                } catch (e: Exception) {
                    viewModel.nextStep()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.configure_battery))
        }
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.skip))
        }
    }
}

@Composable
private fun Step5Features(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.main_features),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        FeatureItem(
            title = stringResource(R.string.quick_add_title),
            description = stringResource(R.string.quick_add_desc)
        )
        
        FeatureItem(
            title = stringResource(R.string.interactive_actions_title),
            description = stringResource(R.string.interactive_actions_desc)
        )
        
        FeatureItem(
            title = stringResource(R.string.smart_search_title),
            description = stringResource(R.string.smart_search_desc)
        )
        
        FeatureItem(
            title = stringResource(R.string.secure_protection_title),
            description = stringResource(R.string.secure_protection_desc)
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.understood))
        }
    }
}

@Composable
private fun FeatureItem(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun Step6Completed(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.setup_completed_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.setup_completed_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { viewModel.completeOnboarding() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.enter_app))
        }
    }
}
