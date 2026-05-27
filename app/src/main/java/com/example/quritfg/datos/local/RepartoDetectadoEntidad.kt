package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repartos_detectados",
    foreignKeys = [
        ForeignKey(
            entity = IngresoDetectadoEntidad::class,
            parentColumns = ["id"],
            childColumns = ["ingresoDetectadoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MetaEntidad::class,
            parentColumns = ["id"],
            childColumns = ["fondoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ingresoDetectadoId"), Index("fondoId")]
)
data class RepartoDetectadoEntidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ingresoDetectadoId: Int,
    val fondoId: Int,
    val fondoNombre: String,
    val cantidadCentimos: Long
)
