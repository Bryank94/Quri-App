package com.example.quritfg.datos.repositorio

import com.example.quritfg.datos.local.*
import kotlinx.coroutines.flow.Flow

/**
 * Capa intermedia entre los ViewModel y la base de datos.
 *
 * Los ViewModel no acceden directamente a Room.
 * Todas las operaciones pasan por este repositorio,
 * lo que mantiene la arquitectura organizada y desacoplada.
 */
class RepositorioQuriRoom(

    // Dependencias necesarias para acceder a cada tabla
    private val metaDao: MetaDao,
    private val gastoDao: GastoDao,
    private val ingresoDao: IngresoDao
) {

    // Meta
    fun obtenerMeta(): Flow<MetaEntidad?> =
        metaDao.obtenerMeta()

    suspend fun guardarMeta(meta: MetaEntidad) =
        metaDao.insertarMeta(meta)

    // Gastos
    fun obtenerGastos(): Flow<List<GastoEntidad>> =
        gastoDao.obtenerGastos()

    suspend fun anadirGasto(gasto: GastoEntidad) =
        gastoDao.insertarGasto(gasto)

    // Ingresos
    fun obtenerIngresos(): Flow<List<IngresoEntidad>> =
        ingresoDao.obtenerIngresos()

    suspend fun anadirIngreso(ingreso: IngresoEntidad) =
        ingresoDao.insertarIngreso(ingreso)
}