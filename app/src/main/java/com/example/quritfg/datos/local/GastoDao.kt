package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO encargado de las operaciones
 * sobre la tabla de gastos en la base de datos.
 */
@Dao
interface GastoDao {

    @Insert
    suspend fun insertarGasto(gasto: GastoEntidad)

    @Query("SELECT * FROM gastos WHERE usuarioId = :usuarioId")
    fun obtenerGastos(usuarioId: Int): Flow<List<GastoEntidad>>

    @Query("UPDATE gastos SET categoria = :categoria, cantidadCentimos = :cantidadCentimos, fecha = :fecha, etiqueta = :etiqueta WHERE id = :gastoId AND usuarioId = :usuarioId")
    suspend fun actualizarGasto(
        gastoId: Int,
        usuarioId: Int,
        categoria: String,
        cantidadCentimos: Long,
        fecha: String,
        etiqueta: String
    )

    @Query("DELETE FROM gastos WHERE id = :gastoId AND usuarioId = :usuarioId")
    suspend fun eliminarGasto(gastoId: Int, usuarioId: Int)

    @Query("SELECT COUNT(*) FROM gastos")
    fun contarTodos(): Flow<Int>

    @Query("DELETE FROM gastos WHERE usuarioId = :usuarioId")
    suspend fun borrarTodos(usuarioId: Int)
}
