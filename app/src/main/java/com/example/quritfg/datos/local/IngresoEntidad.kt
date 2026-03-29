package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un ingreso que el usuario registra en la aplicacion.
 *
 * Cada vez que el usuario añade dinero que recibe,
 * se crea un objeto de esta clase y se guarda en la base de datos.
 */
@Entity(tableName = "ingresos")
data class IngresoEntidad(

    // Identificador unico que se genera automaticamente
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Cantidad de dinero que entra
    val cantidad: Double,

    // Fecha en la que se registra el ingreso
    val fecha: String,

    // Descripcion opcional del ingreso
    val concepto: String? = null
)