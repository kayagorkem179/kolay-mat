package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF082F49),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    error = MathError
)

private val LightColorScheme = lightColorScheme(
    primary = MathPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = MathPrimaryDark,
    secondary = MathSecondary,
    onSecondary = Color.White,
    tertiary = MathAccent,
    onTertiary = Color.White,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    error = MathError
)

private val HighContrastScheme = darkColorScheme(
    primary = HighContrastAccent,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = HighContrastBackground,
    surface = HighContrastSurface,
    onBackground = HighContrastText,
    onSurface = HighContrastText,
    error = Color(0xFFFF4D4D)
)

@Composable
fun KolayMatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isHighContrast -> HighContrastScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    KolayMatTheme(darkTheme = darkTheme, isHighContrast = false, content = content)
}
