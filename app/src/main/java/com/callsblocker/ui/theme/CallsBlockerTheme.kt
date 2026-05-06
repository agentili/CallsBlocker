package com.callsblocker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32), // Forest Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF003300),
    secondary = Color(0xFF4CAF50), 
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF002200),
    tertiary = Color(0xFF388E3C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA5D6A7),
    onTertiaryContainer = Color(0xFF002200),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White, // Pure white for menus and surfaces
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F4F0), // Very light grey-green
    onSurfaceVariant = Color(0xFF444944),
    surfaceContainer = Color(0xFFF5F5F5), // Light grey for menu backgrounds
    outline = Color(0xFF747974),
    outlineVariant = Color(0xFFC2C9C2),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81C784), // Light Green
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFA5D6A7),
    onTertiary = Color(0xFF002200),
    tertiaryContainer = Color(0xFF388E3C),
    onTertiaryContainer = Color(0xFFE8F5E9),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E), // Slightly lighter than background
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFC2C9C2),
    surfaceContainer = Color(0xFF252525), // Distinct dark grey for menus
    outline = Color(0xFF8C938C),
    outlineVariant = Color(0xFF444944),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun CallsBlockerTheme(
    theme: String = "system",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
