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
        IngresoDetectadoEntidad::class,
        RepartoDetectadoEntidad::class,
        UsuarioEntidad::class // nueva tabla para usuarios
    ],
    version = 11,
    exportSchema = true
)
abstract class BaseDatosQuri : RoomDatabase() {

    // DAOs que ya habia
    abstract fun metaDao(): MetaDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun ingresoDetectadoDao(): IngresoDetectadoDao
    abstract fun repartoDetectadoDao(): RepartoDetectadoDao

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

        private val MIGRACION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS metas_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        nombre TEXT NOT NULL,
                        cantidadObjetivoCentimos INTEGER NOT NULL,
                        cantidadActualCentimos INTEGER NOT NULL,
                        FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO metas_nueva (id, usuarioId, nombre, cantidadObjetivoCentimos, cantidadActualCentimos)
                    SELECT id, usuarioId, nombre, ROUND(cantidadObjetivo * 100), ROUND(cantidadActual * 100)
                    FROM metas
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE metas")
                database.execSQL("ALTER TABLE metas_nueva RENAME TO metas")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_metas_usuarioId ON metas(usuarioId)")

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS gastos_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        categoria TEXT NOT NULL,
                        cantidadCentimos INTEGER NOT NULL,
                        fecha TEXT NOT NULL,
                        etiqueta TEXT NOT NULL,
                        FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO gastos_nueva (id, usuarioId, categoria, cantidadCentimos, fecha, etiqueta)
                    SELECT id, usuarioId, categoria, ROUND(cantidad * 100), fecha, etiqueta
                    FROM gastos
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE gastos")
                database.execSQL("ALTER TABLE gastos_nueva RENAME TO gastos")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_gastos_usuarioId ON gastos(usuarioId)")

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ingresos_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        cantidadCentimos INTEGER NOT NULL,
                        fecha TEXT NOT NULL,
                        concepto TEXT,
                        FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO ingresos_nueva (id, usuarioId, cantidadCentimos, fecha, concepto)
                    SELECT id, usuarioId, ROUND(cantidad * 100), fecha, concepto
                    FROM ingresos
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE ingresos")
                database.execSQL("ALTER TABLE ingresos_nueva RENAME TO ingresos")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_ingresos_usuarioId ON ingresos(usuarioId)")
            }
        }

        private val MIGRACION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP INDEX IF EXISTS index_metas_usuarioId")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_metas_usuarioId ON metas(usuarioId)")
            }
        }

        private val MIGRACION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE metas ADD COLUMN fechaLimite TEXT NOT NULL DEFAULT '07-05-2027'")
            }
        }

        private val MIGRACION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE metas ADD COLUMN prioridad INTEGER NOT NULL DEFAULT 2")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ingresos_detectados (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        concepto TEXT NOT NULL,
                        entidad TEXT NOT NULL,
                        cantidadCentimos INTEGER NOT NULL,
                        fecha TEXT NOT NULL,
                        repartoResumen TEXT NOT NULL,
                        totalAsignadoCentimos INTEGER NOT NULL,
                        confirmado INTEGER NOT NULL,
                        FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_ingresos_detectados_usuarioId ON ingresos_detectados(usuarioId)")
            }
        }

        private val MIGRACION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ingresos_detectados ADD COLUMN deshecho INTEGER NOT NULL DEFAULT 0")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS repartos_detectados (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        ingresoDetectadoId INTEGER NOT NULL,
                        fondoId INTEGER NOT NULL,
                        fondoNombre TEXT NOT NULL,
                        cantidadCentimos INTEGER NOT NULL,
                        FOREIGN KEY(ingresoDetectadoId) REFERENCES ingresos_detectados(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(fondoId) REFERENCES metas(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_repartos_detectados_ingresoDetectadoId ON repartos_detectados(ingresoDetectadoId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_repartos_detectados_fondoId ON repartos_detectados(fondoId)")
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
                    .addMigrations(
                        MIGRACION_4_5,
                        MIGRACION_5_6,
                        MIGRACION_6_7,
                        MIGRACION_7_8,
                        MIGRACION_8_9,
                        MIGRACION_9_10,
                        MIGRACION_10_11
                    )
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}
