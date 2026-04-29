package com.callsblocker.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.callsblocker.ui.screens.HomeScreen
import com.callsblocker.ui.screens.SettingsScreen
import com.callsblocker.ui.screens.InfoScreen
import com.callsblocker.ui.screens.BlockedCallsScreen
import com.callsblocker.ui.theme.CallsBlockerTheme
import com.callsblocker.util.AppRoleManager
import com.callsblocker.util.BatteryOptimizationManager
import com.callsblocker.util.PrefsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var roleManager: AppRoleManager

    @Inject
    lateinit var batteryManager: BatteryOptimizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Check if onboarding is completed (read once, not as a continuous flow)
        val prefsManager = PrefsManager(this)
        lifecycleScope.launch {
            val isCompleted = prefsManager.onboardingCompleted.first()
            if (!isCompleted) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish()
            } else {
                setMainContent()
            }
        }
    }

    private fun setMainContent() {
        setContent {
            CallsBlockerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        val viewModel = hiltViewModel<MainViewModel>()
                        HomeScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                    composable("settings") {
                        val viewModel = hiltViewModel<MainViewModel>()
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel,
                            roleManager = roleManager,
                            batteryManager = batteryManager
                        )
                    }
                    composable("info") {
                        InfoScreen(navController = navController)
                    }
                    composable("logs") {
                        val viewModel = hiltViewModel<MainViewModel>()
                        BlockedCallsScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
