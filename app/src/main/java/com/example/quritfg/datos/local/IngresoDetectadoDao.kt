package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDetectadoDao {
    @Insert
    suspend fun insertarIngresoDetectado(ingreso: IngresoDetectadoEntidad): Long

    @Query("SELECT * FROM ingresos_detectados WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun obtenerIngresosDetectados(usuarioId: Int): Flow<List<IngresoDetectadoEntidad>>

    @Query("UPDATE ingresos_detectados SET deshecho = 1 WHERE id = :ingresoDetectadoId AND usuarioId = :usuarioId")
    suspend fun marcarDeshecho(ingresoDetectadoId: Int, usuarioId: Int)

    @Query("DELETE FROM ingresos_detectados WHERE usuarioId = :usuarioId")
    suspend fun borrarTodos(usuarioId: Int)
}
