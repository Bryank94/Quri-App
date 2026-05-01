package com.example.quritfg.ui.componentes

import androidx.compose.ui.graphics.Color

fun colorProgreso(porcentajeProgreso: Float): Color {
    return when {
        porcentajeProgreso < 0.5f -> Color(0xFFE53935)
        porcentajeProgreso < 0.7f -> Color(0xFF43A047)
        else -> Color(0xFF1E88E5)
    }
}
