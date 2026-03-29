package com.example.quritfg.ui.modelo

import com.example.quritfg.datos.local.GastoEntidad

data class HistorialMes(
    val mes: String,
    val gastos: List<GastoEntidad>
)