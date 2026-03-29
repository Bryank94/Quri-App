package com.example.quritfg.ui.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quritfg.ui.componentes.BarraNavegacionInferior

/**
 * Funcion principal que organiza la estructura visual
 * y la navegacion general de la aplicacion.
 */
@Composable
fun QuriApp() {

    // Controlador de navegacion principal
    val navController = rememberNavController()

    // Detecta en que pantalla estamos actualmente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // Pantallas donde no se muestra la barra inferior
    val rutasSinBarra = setOf(
        Rutas.Registro.ruta,
        Rutas.ConfiguracionMeta.ruta
    )

    // Decide si la barra debe mostrarse o no
    val mostrarBarraInferior = rutaActual !in rutasSinBarra

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraNavegacionInferior(navController)
            }
        }
    ) { padding ->

        // Contenido principal de la app
        GrafoNavegacion(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}