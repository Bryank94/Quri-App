package com.example.quritfg.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MetaEntidad::class,
        GastoEntidad::class,
        IngresoEntidad::class,
        UsuarioEntidad::class // 🔥 NUEVO
    ],
    version = 3 // 🔥 IMPORTANTE (antes era 2)
)
abstract class BaseDatosQuri : RoomDatabase() {

    // DAOs existentes
    abstract fun metaDao(): MetaDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao

    // 🔥 NUEVO DAO
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCIA: BaseDatosQuri? = null

        fun obtenerBaseDatos(context: Context): BaseDatosQuri {

            return INSTANCIA ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosQuri::class.java,
                    "quri_base_datos"
                )
                    .fallbackToDestructiveMigration() // 🔥 borra datos al cambiar versión
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}