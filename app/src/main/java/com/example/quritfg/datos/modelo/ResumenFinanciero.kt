package com.example.quritfg.datos.modelo

data class ResumenFinanciero(
    val totalIngresos: Double,
    val totalGastos: Double,
    val ahorroActual: Double,
    val objetivo: Double,
    val porcentajeProgreso: Float,
    val ahorroRestante: Double
)