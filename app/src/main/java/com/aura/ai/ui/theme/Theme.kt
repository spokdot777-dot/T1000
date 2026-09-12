package com.aura.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val T1000ColorScheme = darkColorScheme(
    primary = MintAccent,
    onPrimary = OnAccent,
    primaryContainer = MintDim,
    onPrimaryContainer = OnAccent,
    secondary = Graphite600,
    onSecondary = TextPrimary,
    background = Graphite950,
    onBackground = TextPrimary,
    surface = Graphite900,
    onSurface = TextPrimary,
    surfaceVariant = Graphite800,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = Graphite850,
    surfaceContainerHigh = Graphite800,
    outline = Graphite700,
    outlineVariant = Graphite700,
    error = RoseError,
    onError = OnAccent,
)

@Composable
fun T1000Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // T1000 is intentionally a single, committed dark aesthetic regardless of
    // system setting — the identity depends on it.
    MaterialTheme(
        colorScheme = T1000ColorScheme,
        typography = T1000Typography,
        content = content,
    )
}
