package com.callsblocker.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.callsblocker.ui.screens.HomeScreen
import com.callsblocker.ui.screens.SettingsScreen
import com.callsblocker.ui.screens.InfoScreen
import com.callsblocker.ui.screens.BlockedCallsScreen
import com.callsblocker.ui.screens.CallLogScreen
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
            val viewModel = hiltViewModel<MainViewModel>()
            val uiState by viewModel.uiState.collectAsState()
            
            // Apply language preference
            LaunchedEffect(uiState.appLanguage) {
                val appLocale: LocaleListCompat = if (uiState.appLanguage == "system") {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(uiState.appLanguage)
                }
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            
            CallsBlockerTheme(theme = uiState.appTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    bottomBar = {
                        val showBottomBar = currentDestination?.route in listOf("home", "call_log")
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                                    label = { Text(androidx.compose.ui.res.stringResource(id = com.callsblocker.R.string.blacklist)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                                    label = { Text(androidx.compose.ui.res.stringResource(id = com.callsblocker.R.string.history)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == "call_log" } == true,
                                    onClick = {
                                        navController.navigate("call_log") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable("call_log") {
                            CallLogScreen(viewModel = viewModel)
                        }
                        composable("settings") {
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
}
