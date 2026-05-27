package com.example.quritfg.datos.modelo

/**
 * Representa un gasto dentro de la logica de la aplicacion.
 *
 * Este modelo se usa a nivel interno cuando se trabaja
 * con gastos en memoria o en la capa de negocio.
 */
data class Gasto(

    val categoria: String,
    val cantidadCentimos: Long,
    val fecha: String
)
