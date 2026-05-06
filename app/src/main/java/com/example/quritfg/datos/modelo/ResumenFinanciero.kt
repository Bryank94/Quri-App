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
    val totalIngresos: Double,

    // suma total de gastos
    val totalGastos: Double,

    // dinero ahorrado actualmente
    val ahorroActual: Double,

    // objetivo de ahorro definido
    val objetivo: Double,

    // porcentaje de progreso hacia el objetivo
    val porcentajeProgreso: Float,

    // lo que falta por ahorrar
    val ahorroRestante: Double
)