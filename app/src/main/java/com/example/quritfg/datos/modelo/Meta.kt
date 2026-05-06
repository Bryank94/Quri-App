package com.example.quritfg.datos.modelo

/**
 * Representa la meta de ahorro dentro de la logica de la aplicacion.
 *
 * Se usa para manejar la informacion de la meta
 * sin depender directamente de la base de datos.
 */
data class Meta(
    val nombre: String,
    val cantidadObjetivo: Double,
    val cantidadActual: Double = 0.0
)