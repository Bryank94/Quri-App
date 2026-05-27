package com.example.quritfg.datos.modelo

/**
 * Representa un resumen general de la situacion financiera.
 *
 * Esto no es una tabla de la base de datos, es mas bien un modelo
 * que se usa para agrupar datos ya calculados (totales, progreso, etc).
 *
 * Sirve por ejemplo para mostrar un resumen en pantalla.
 */
data class ResumenFinanciero(

    // suma total de ingresos
    val totalIngresosCentimos: Long,

    // suma total de gastos
    val totalGastosCentimos: Long,

    // dinero ahorrado actualmente
    val ahorroActualCentimos: Long,

    // objetivo de ahorro definido
    val objetivoCentimos: Long,

    // porcentaje de progreso hacia el objetivo
    val porcentajeProgreso: Float,

    // lo que falta por ahorrar
    val ahorroRestanteCentimos: Long
)
