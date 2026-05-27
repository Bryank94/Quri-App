package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingresos_detectados",
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
data class IngresoDetectadoEntidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int = 0,
    val concepto: String,
    val entidad: String,
    val cantidadCentimos: Long,
    val fecha: String,
    val repartoResumen: String,
    val totalAsignadoCentimos: Long,
    val confirmado: Boolean = false,
    val deshecho: Boolean = false
)
