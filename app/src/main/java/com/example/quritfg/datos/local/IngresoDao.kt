package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones sobre la tabla de ingresos.
 *
 * Permite guardar nuevos ingresos y consultar
 * los ingresos almacenados en la base de datos.
 */
@Dao
interface IngresoDao {

    /**
     * Inserta un ingreso cuando el usuario lo registra.
     */
    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntidad)

    /**
     * Devuelve todos los ingresos almacenados.
     * Se expone como Flow para que la interfaz
     * se actualice automaticamente ante cambios.
     */
    @Query("SELECT * FROM ingresos")
    fun obtenerIngresos(): Flow<List<IngresoEntidad>>
}