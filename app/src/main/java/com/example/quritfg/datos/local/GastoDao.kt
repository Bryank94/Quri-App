package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO encargado de las operaciones
 * sobre la tabla de gastos en la base de datos.
 *
 * se insertan y se consultan los gastos.
 */
@Dao
interface GastoDao {

    /**
     * Inserta un nuevo gasto en la base de datos.
     *
     * Es suspend porque se ejecuta en segundo plano
     * mediante corrutinas.
     */
    @Insert
    suspend fun insertarGasto(gasto: GastoEntidad)

    /**
     * Devuelve todos los gastos almacenados.
     *
     * Se utiliza Flow para que la interfaz se actualice
     * automáticamente cuando cambian los datos.
     */
    @Query("SELECT * FROM gastos WHERE usuarioId = :usuarioId")
    fun obtenerGastos(usuarioId: Int): Flow<List<GastoEntidad>>

    /**
     * Borra todos los gastos guardados.
     */
    @Query("DELETE FROM gastos WHERE usuarioId = :usuarioId")
    suspend fun borrarTodos(usuarioId: Int)
}
