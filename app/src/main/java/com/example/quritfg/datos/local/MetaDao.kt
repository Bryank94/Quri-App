package com.example.quritfg.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Se encarga de guardar y leer la meta de ahorro del usuario.
 */
@Dao
interface MetaDao {

    /**
     * Guarda la meta en la base de datos.
     * Si ya existe una, la reemplaza por la nueva.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMeta(meta: MetaEntidad): Long

    /**
     * Devuelve la meta actual almacenada.
     * Solo se obtiene una porque la app trabaja
     * con una unica meta activa.
     */
    @Query("SELECT * FROM metas WHERE usuarioId = :usuarioId LIMIT 1")
    fun obtenerMeta(usuarioId: Int): Flow<MetaEntidad?>

    @Query("SELECT * FROM metas WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun obtenerFondos(usuarioId: Int): Flow<List<MetaEntidad>>

    @Query("SELECT COUNT(*) FROM metas")
    fun contarTodas(): Flow<Int>

    @Query(
        """
        UPDATE metas
        SET cantidadActualCentimos = cantidadActualCentimos + :cantidadCentimos
        WHERE id = :fondoId AND usuarioId = :usuarioId
        """
    )
    suspend fun aportarAFondo(fondoId: Int, usuarioId: Int, cantidadCentimos: Long)

    @Query(
        """
        UPDATE metas
        SET cantidadActualCentimos = MAX(cantidadActualCentimos - :cantidadCentimos, 0)
        WHERE id = :fondoId AND usuarioId = :usuarioId
        """
    )
    suspend fun restarAFondo(fondoId: Int, usuarioId: Int, cantidadCentimos: Long)

    @Query("DELETE FROM metas WHERE id = :fondoId AND usuarioId = :usuarioId")
    suspend fun eliminarFondo(fondoId: Int, usuarioId: Int)

    /**
     * Borra las metas guardadas.
     */
    @Query("DELETE FROM metas WHERE usuarioId = :usuarioId")
    suspend fun borrarTodas(usuarioId: Int)
}

