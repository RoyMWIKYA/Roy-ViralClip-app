package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CyberColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = PremiumBg,
    secondary = ElectricCyan,
    onSecondary = PremiumBg,
    tertiary = GoldWarm,
    background = PremiumBg,
    onBackground = WhiteSoft,
    surface = SurfaceDark,
    onSurface = WhiteSoft,
    surfaceVariant = SurfaceDarkLighter,
    onSurfaceVariant = WhiteSoft,
    error = ErrorCrimson,
    onError = PremiumBg,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark layout for cyber creator workspace
    dynamicColor: Boolean = false, // Disable dynamic light schemes since we want dark vibe
    content: @Composable () -> Unit,
) {
    val colorScheme = CyberColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
