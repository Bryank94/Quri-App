package com.example.quritfg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🔹 Light theme (blanco)
private val LightColorScheme = lightColorScheme(
    primary = VerdeDinero,
    onPrimary = Blanco,
    primaryContainer = Color(0xFFE6F4EC),
    onPrimaryContainer = Color(0xFF082A17),

    secondary = DoradoDinero,
    onSecondary = Negro,
    secondaryContainer = Color(0xFFFFF4D6),
    onSecondaryContainer = Color(0xFF3B2A00),

    error = RojoPeligro,
    errorContainer = RojoClaro,
    onError = Blanco,

    background = Color(0xFFF2F7EF),
    onBackground = Color(0xFF06160D),

    surface = Color(0xEAF8FFF4),
    onSurface = Color(0xFF06160D),
    surfaceVariant = Color(0xFFE6F4EC),
    onSurfaceVariant = Color(0xFF12351F)
)

// 🔹 Dark theme (negro)
private val DarkColorScheme = darkColorScheme(
    primary = VerdeDinero,
    onPrimary = Blanco,
    primaryContainer = Color(0x660A5E31),
    onPrimaryContainer = Blanco,

    secondary = DoradoDinero,
    onSecondary = Negro,

    error = RojoPeligro,
    onError = Blanco,

    background = Color(0xFF06160D),
    onBackground = Blanco,

    surface = Color(0x77061C10),
    onSurface = Blanco,
    surfaceVariant = Color(0x663C6D45),
    onSurfaceVariant = Blanco,
    secondaryContainer = Color(0x66D6A11E)
)

@Composable
fun QuriTFGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 🚨 DESACTIVADO
    content: @Composable () -> Unit
) {

    // 🔥 Eliminamos completamente dynamic color
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
