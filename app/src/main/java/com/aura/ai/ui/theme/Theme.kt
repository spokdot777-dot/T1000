package com.aura.ai.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = PrimaryDark,
    primaryContainer = SecondaryDark,
    onPrimaryContainer = AccentColor,
    secondary = SecondaryDark,
    onSecondary = AccentColor,
    tertiary = AccentColor,
    onTertiary = PrimaryDark,
    error = ErrorColor,
    onError = PrimaryLight,
    background = PrimaryDark,
    onBackground = PrimaryLight,
    surface = SecondaryDark,
    onSurface = PrimaryLight,
    surfaceVariant = PrimaryDark,
    onSurfaceVariant = AccentColor,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentColor,
    onPrimary = PrimaryLight,
    primaryContainer = SecondaryLight,
    onPrimaryContainer = AccentColor,
    secondary = SecondaryLight,
    onSecondary = AccentColor,
    tertiary = AccentColor,
    onTertiary = PrimaryLight,
    error = ErrorColor,
    onError = PrimaryLight,
    background = PrimaryLight,
    onBackground = PrimaryDark,
    surface = SecondaryLight,
    onSurface = PrimaryDark,
    surfaceVariant = PrimaryLight,
    onSurfaceVariant = AccentColor,
)

@Composable
fun T1000Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
