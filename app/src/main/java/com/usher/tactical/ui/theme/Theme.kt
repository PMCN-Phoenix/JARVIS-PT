package com.usher.tactical.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TacticalColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentAmber,
    error = WarningRed,
    background = BgPrimary,
    surface = SurfaceCard,
    onPrimary = BgPrimary,
    onSecondary = BgPrimary,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onError = TextWhite
)

@Composable
fun TacticalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TacticalColorScheme,
        typography = TacticalTypography,
        content = content
    )
}
