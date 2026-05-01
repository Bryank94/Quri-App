package com.example.quritfg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// 🔹 Light theme (blanco)
private val LightColorScheme = lightColorScheme(
    primary = Negro,
    onPrimary = Blanco,

    background = Blanco,
    onBackground = Negro,

    surface = GrisClaro,
    onSurface = Negro
)

// 🔹 Dark theme (negro)
private val DarkColorScheme = darkColorScheme(
    primary = Blanco,
    onPrimary = Negro,

    background = Negro,
    onBackground = Blanco,

    surface = GrisOscuro,
    onSurface = Blanco
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