package com.lacolinares.jetpackqrbarcodescanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = SpaceCadet,
    secondary = LightSteelBlue,
    tertiary = LightSteelBlue
)

@Composable
fun JetpackQRBarcodeScannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}