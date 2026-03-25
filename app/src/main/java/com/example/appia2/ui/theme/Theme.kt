package com.example.appia2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cores básicas (você pode mudar os hexadecimais depois)
val LightBlue = Color(0xFF2196F3)
val DarkBlue = Color(0xFF1976D2)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = DarkBlue
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = LightBlue
)

@Composable
fun AppIA2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
