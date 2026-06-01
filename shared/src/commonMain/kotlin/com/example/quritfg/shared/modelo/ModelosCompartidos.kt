package com.example.quritfg.shared.modelo

data class MovimientoFinanciero(
    val cantidadCentimos: Long,
    val fecha: String,
    val categoria: String = "",
    val etiqueta: String = ""
)

data class FondoAhorro(
    val id: Int = 0,
    val nombre: String,
    val cantidadObjetivoCentimos: Long,
    val cantidadActualCentimos: Long = 0L,
    val fechaLimite: String = "",
    val prioridad: Int = 2
)
