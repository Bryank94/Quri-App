package com.example.quritfg.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Base de datos principal de la app.
 *
 * Aqui es donde Room guarda todo lo importante:
 * gastos, ingresos, metas y ahora tambien usuarios.
 *
 * Basicamente es el punto central desde donde se accede
 * a toda la info guardada en local.
 */
@Database(
    entities = [
        MetaEntidad::class,
        GastoEntidad::class,
        IngresoEntidad::class,
        UsuarioEntidad::class // nueva tabla para usuarios
    ],
    version = 6,
    exportSchema = true
)
abstract class BaseDatosQuri : RoomDatabase() {

    // DAOs que ya habia
    abstract fun metaDao(): MetaDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao

    /**
     * DAO nuevo para manejar usuarios.
     * desde aqui se pueden hacer inserts, consultas, etc
     */
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE usuarios ADD COLUMN authVersion INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE usuarios ADD COLUMN passwordIterations INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRACION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE gastos ADD COLUMN etiqueta TEXT NOT NULL DEFAULT 'Necesario'")
            }
        }

        /**
         * Se usa volatile para evitar problemas con multiples hilos
         * y asegurar que solo haya una instancia real
         */
        @Volatile
        private var INSTANCIA: BaseDatosQuri? = null

        /**
         * Devuelve la base de datos.
         *
         * Si ya existe la reutiliza, si no la crea.
         * Esto evita crear varias instancias innecesarias.
         */
        fun obtenerBaseDatos(context: Context): BaseDatosQuri {

            return INSTANCIA ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosQuri::class.java,
                    "quri_base_datos"
                )
                    .addMigrations(MIGRACION_4_5, MIGRACION_5_6)
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}
