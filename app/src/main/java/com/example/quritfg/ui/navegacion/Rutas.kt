package com.example.quritfg.ui.navegacion

/**
 * Define todas las rutas de navegacion de la aplicacion.
 *
 * Cada objeto representa una pantalla y su identificador interno.
 */
sealed class Rutas(val ruta: String) {

    object Registro : Rutas("registro")

    object ConfiguracionMeta : Rutas("configuracion_meta")

    object Inicio : Rutas("inicio")

    object AnadirGasto : Rutas("anadir_gasto")

    object Progreso : Rutas("progreso")

    object Historial : Rutas("historial")

    object Metas : Rutas("metas")
}