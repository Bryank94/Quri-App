package com.example.quritfg.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase principal que define la base de datos de la aplicación Quri.
 *
 * Esta clase extiende de RoomDatabase y actúa como punto central de acceso
 * a las tablas persistentes del sistema.
 *
 * Se configura con tres entidades:
 *  - MetaEntidad
 *  - GastoEntidad
 *  - IngresoEntidad
 *
 * La versión actual de la base de datos es 2.
 */
@Database(
    entities = [
        MetaEntidad::class,
        GastoEntidad::class,
        IngresoEntidad::class
    ],
    version = 2
)
abstract class BaseDatosQuri : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de metas.
     * Permite realizar operaciones CRUD sobre la tabla "metas".
     */
    abstract fun metaDao(): MetaDao

    /**
     * Proporciona acceso al DAO de gastos.
     * Permite insertar y consultar gastos almacenados en Room.
     */
    abstract fun gastoDao(): GastoDao

    /**
     * Proporciona acceso al DAO de ingresos.
     * Permite insertar y consultar ingresos almacenados en Room.
     */
    abstract fun ingresoDao(): IngresoDao

    companion object {

        /**
         * Instancia única de la base de datos.
         *
         * Se marca como @Volatile para garantizar visibilidad
         * entre hilos cuando se accede en entornos concurrentes.
         */
        @Volatile
        private var INSTANCIA: BaseDatosQuri? = null

        /**
         * Devuelve una instancia única de la base de datos.
         *
         * Implementa el patrón Singleton para asegurar que solo exista
         * una instancia de Room en toda la aplicación.
         *
         * @param context Contexto de la aplicación necesario para crear la base de datos.
         * @return Instancia única de BaseDatosQuri.
         */
        fun obtenerBaseDatos(context: Context): BaseDatosQuri {

            // Si la instancia ya existe, se devuelve directamente.
            // Si no, se crea de forma sincronizada para evitar múltiples creaciones simultáneas.
            return INSTANCIA ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosQuri::class.java,
                    "quri_base_datos"
                )
                    /**
                     * fallbackToDestructiveMigration()
                     *
                     * Si cambia la versión de la base de datos y no existe
                     * una migración definida, Room eliminará las tablas
                     * anteriores y las recreará.
                     *
                     */
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}