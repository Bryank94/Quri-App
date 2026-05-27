package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones sobre la tabla de ingresos.
 *
 * Permite guardar, editar, borrar y consultar
 * los ingresos almacenados en la base de datos.
 */
@Dao
interface IngresoDao {

    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntidad)

    @Query("SELECT * FROM ingresos WHERE usuarioId = :usuarioId")
    fun obtenerIngresos(usuarioId: Int): Flow<List<IngresoEntidad>>

    @Query("UPDATE ingresos SET cantidadCentimos = :cantidadCentimos, fecha = :fecha, concepto = :concepto WHERE id = :ingresoId AND usuarioId = :usuarioId")
    suspend fun actualizarIngreso(
        ingresoId: Int,
        usuarioId: Int,
        cantidadCentimos: Long,
        fecha: String,
        concepto: String?
    )

    @Query("DELETE FROM ingresos WHERE id = :ingresoId AND usuarioId = :usuarioId")
    suspend fun eliminarIngreso(ingresoId: Int, usuarioId: Int)

    @Query("SELECT COUNT(*) FROM ingresos")
    fun contarTodos(): Flow<Int>

    @Query("DELETE FROM ingresos WHERE usuarioId = :usuarioId")
    suspend fun borrarTodos(usuarioId: Int)
}
