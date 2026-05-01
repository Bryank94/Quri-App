package com.example.quritfg.ui.modelo

import com.example.quritfg.datos.local.GastoEntidad

/**
 * Modelo para agrupar gastos por mes.
 *
 * Se usa para mostrar el historial organizado,
 * por ejemplo en listas tipo "enero", "febrero", etc.
 */
data class HistorialMes(

    // nombre del mes
    val mes: String,

    // lista de gastos de ese mes
    val gastos: List<GastoEntidad>
)