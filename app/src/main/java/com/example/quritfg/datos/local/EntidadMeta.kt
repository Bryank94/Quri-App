package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa la meta de ahorro configurada por el usuario.
 *
 * Esta entidad se almacena en la tabla "metas" y permite
 * guardar el objetivo económico que el usuario quiere alcanzar.
 */
@Entity(
    tableName = "metas",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntidad::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"], unique = true)]
)
data class MetaEntidad(

    // Identificador  generado automáticamente por Room
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: Int = 0,

    // Nombre descriptivo de la meta (por ejemplo: "Viaje", "Coche")
    val nombre: String,

    // Cantidad que el usuario quiere alcanzar
    val cantidadObjetivo: Double,

    // Progreso acumulado hacia esa meta
    val cantidadActual: Double
)
