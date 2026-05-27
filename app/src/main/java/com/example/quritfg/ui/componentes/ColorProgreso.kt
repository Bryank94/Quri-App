package com.example.quritfg.ui.componentes

import androidx.compose.ui.graphics.Color
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.RojoPeligro
import com.example.quritfg.ui.theme.VerdeDinero

fun colorProgreso(porcentajeProgreso: Float): Color {
    return when {
        porcentajeProgreso < 0.4f -> RojoPeligro
        porcentajeProgreso < 0.75f -> DoradoDinero
        else -> VerdeDinero
    }
}
