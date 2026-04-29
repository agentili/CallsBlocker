package com.callsblocker.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

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
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
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
                5 -> Step5Completed(viewModel = viewModel)
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
            text = "Benvenuto in CallsBlocker",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Questa app ti aiuta a bloccare le chiamate indesiderate con una blacklist personalizzata.\n\nTocca avanti per iniziare la configurazione.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Avanti")
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
            text = "Accesso al registro chiamate",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Per permetterti di aggiungere numeri dal registro delle chiamate recenti, abbiamo bisogno di accedere al tuo registro chiamate.",
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
            Text("Concedi accesso")
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
            text = "Impostazione come app di screening",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Per bloccare effettivamente le chiamate, devi impostare CallsBlocker come gestore schermata chiamate. Questo permetterà all'app di intercettare le chiamate in arrivo.",
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
            Text("Imposta come gestore")
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
            text = "Esenzione da ottimizzazione batteria",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Alcuni dispositivi sospendono le app in background per risparmiare batteria. Per garantire che CallsBlocker continui a bloccare le chiamate, ti consigliamo di esentare l'app dall'ottimizzazione batteria.",
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
            Text("Configura batteria")
        }
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Salta")
        }
    }
}

@Composable
private fun Step5Completed(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurazione completata!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "CallsBlocker è ora pronto per bloccare le tue chiamate indesiderate. Puoi iniziare ad aggiungere numeri alla blacklist.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { viewModel.completeOnboarding() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Accedi all'app")
        }
    }
}
