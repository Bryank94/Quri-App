package com.example.quritfg.ui.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.quritfg.datos.SesionManager
import com.example.quritfg.ui.componentes.BarraNavegacionInferior
import com.example.quritfg.ui.componentes.BarraSuperior

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

    /**
     * Decide a que pantalla entrar al iniciar la app.
     *
     * Si esta logueado -> va a inicio
     * si no -> va a registro
     */
    val startDestination = if (sesionManager.estaLogueado()) {
        Rutas.Inicio.ruta
    } else {
        Rutas.Registro.ruta
    }

    // detecta la pantalla actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    /**
     * Pantallas donde NO se muestra la barra inferior
     * (login, registro, config inicial, etc)
     */
    val rutasSinBarra = setOf(
        Rutas.Registro.ruta,
        Rutas.Login.ruta,
        Rutas.ConfiguracionMeta.ruta
    )

    // decide si se muestra o no la barra inferior
    val mostrarBarraInferior = rutaActual !in rutasSinBarra

    Scaffold(

        // barra de arriba (siempre visible)
        topBar = {
            BarraSuperior()
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