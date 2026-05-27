package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RepartoDetectadoDao {
    @Insert
    suspend fun insertarRepartos(repartos: List<RepartoDetectadoEntidad>)

    @Query("SELECT * FROM repartos_detectados WHERE ingresoDetectadoId = :ingresoDetectadoId")
    suspend fun obtenerRepartosPorIngreso(ingresoDetectadoId: Int): List<RepartoDetectadoEntidad>

    @Query("DELETE FROM repartos_detectados WHERE ingresoDetectadoId = :ingresoDetectadoId")
    suspend fun borrarPorIngreso(ingresoDetectadoId: Int)
}
