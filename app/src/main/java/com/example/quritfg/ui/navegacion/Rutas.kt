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

    object Onboarding : Rutas("onboarding")

    object AnadirGasto : Rutas("anadir_gasto")

    object Progreso : Rutas("progreso")

    object Historial : Rutas("historial")

    object Metas : Rutas("metas")

    object Banco : Rutas("banco")

    object PlanMensual : Rutas("plan_mensual")

    object Finanzas : Rutas("finanzas")

    object Perfil : Rutas("perfil")

    object Login : Rutas("login")

    object Admin : Rutas("admin")
}
