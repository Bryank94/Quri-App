package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un gasto almacenado en la base de datos.
 *
 * Esta clase es el modelo que Room utiliza para guardar
 * los gastos de forma persistente.
 *
 * Cada vez que el usuario añade un gasto desde la aplicación,
 * se crea un objeto de esta clase y se almacena en la tabla "gastos".
 */
@Entity(tableName = "gastos")
data class GastoEntidad(

    /**
     * Identificador único del gasto.
     * Se genera automáticamente al insertarlo en la base de datos.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Información descriptiva del gasto.
     */
    val categoria: String,

    /**
     * Cantidad económica asociada al gasto.
     */
    val cantidad: Double,

    /**
     * Fecha en la que se registró el gasto.
     */
    val fecha: String
)