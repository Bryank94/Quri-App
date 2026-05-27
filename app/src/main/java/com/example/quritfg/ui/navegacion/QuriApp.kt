package com.example.quritfg.ui.navegacion

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.modelo.RecompensasQuri
import com.example.quritfg.ui.componentes.BarraNavegacionInferior
import com.example.quritfg.ui.componentes.BarraSuperior
import com.example.quritfg.ui.componentes.FondoQuri

/**
 * Componente principal de la app.
 *
 * Aqui se gestiona la navegacion y la estructura general:
 * barra superior, inferior y pantallas.
 */
@Composable
fun QuriApp() {

    // controlador de navegacion
    val navController = rememberNavController()

    // gestiona la sesion del usuario
    val context = LocalContext.current
    val sesionManager = SesionManager(context)
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val analytics = remember { LocalAnalyticsTracker(context) }
    val esBuildDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Decide a que pantalla entrar al iniciar la app.
     *
     * Si esta logueado -> va a inicio
     * si no -> va a registro
     */
    val startDestination = if (sesionManager.estaLogueado()) {
        if (sesionManager.onboardingVisto()) Rutas.Inicio.ruta else Rutas.Onboarding.ruta
    } else {
        Rutas.Registro.ruta
    }

    // detecta la pantalla actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    LaunchedEffect(rutaActual) {
        rutaActual?.let { ruta ->
            analytics.track(QuriAnalyticsEvents.SCREEN_VIEWED, mapOf("screen" to ruta))
        }
    }

    /**
     * Pantallas donde NO se muestra la barra inferior
     * (login, registro, config inicial, etc)
     */
    val rutasSinBarra = setOf(
        Rutas.Registro.ruta,
        Rutas.Login.ruta,
        Rutas.Onboarding.ruta,
        Rutas.ConfiguracionMeta.ruta,
        Rutas.Admin.ruta
    )

    // decide si se muestra o no la barra inferior
    val mostrarBarraInferior = rutaActual !in rutasSinBarra
    val mostrarMenuUsuario = rutaActual !in setOf(
        Rutas.Registro.ruta,
        Rutas.Login.ruta,
        Rutas.Onboarding.ruta,
        Rutas.ConfiguracionMeta.ruta,
        Rutas.Admin.ruta
    )
    val mostrarAccesoAdmin = esBuildDebug && rutaActual in setOf(
        Rutas.Registro.ruta,
        Rutas.Login.ruta
    )
    val ingresos by repositorio.obtenerIngresos().collectAsState(initial = emptyList())
    val gastos by repositorio.obtenerGastos().collectAsState(initial = emptyList())
    val fondos by repositorio.obtenerFondos().collectAsState(initial = emptyList())
    val puntosQuri = if (mostrarMenuUsuario) {
        RecompensasQuri.calcularPuntos(ingresos, gastos, fondos)
    } else {
        null
    }

    FondoQuri {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,

        // barra de arriba (siempre visible)
        topBar = {
            BarraSuperior(
                mostrarMenuUsuario = mostrarMenuUsuario,
                puntosQuri = puntosQuri,
                onAdminClick = if (mostrarAccesoAdmin) {
                    { navController.navigate(Rutas.Admin.ruta) }
                } else {
                    null
                },
                onLogoClick = if (mostrarMenuUsuario) {
                    {
                    sesionManager.cerrarSesion()

                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    }
                } else {
                    null
                }
            )
        },

        // barra inferior solo en ciertas pantallas
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraNavegacionInferior(navController)
            }
        }

    ) { padding ->

        // aqui se carga toda la navegacion de pantallas
        GrafoNavegacion(
            navController = navController,
            startDestination = startDestination, // esto es clave
            modifier = Modifier.padding(padding)
        )
    }
    }
}

