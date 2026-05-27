package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un ingreso que el usuario registra en la aplicacion.
 *
 * Cada vez que el usuario añade dinero que recibe,
 * se crea un objeto de esta clase y se guarda en la base de datos.
 */
@Entity(
    tableName = "ingresos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntidad::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class IngresoEntidad(

    // Identificador unico que se genera automaticamente
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: Int = 0,

    // Cantidad de dinero que entra
    val cantidadCentimos: Long,

    // Fecha en la que se registra el ingreso
    val fecha: String,

    // Descripcion opcional del ingreso
    val concepto: String? = null
)
