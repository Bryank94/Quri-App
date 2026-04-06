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

@Composable
fun QuriApp() {

    val navController = rememberNavController()

    // 🔥 SESIÓN
    val context = LocalContext.current
    val sesionManager = SesionManager(context)

    val startDestination = if (sesionManager.estaLogueado()) {
        Rutas.Inicio.ruta
    } else {
        Rutas.Registro.ruta
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val rutasSinBarra = setOf(
        Rutas.Registro.ruta,
        Rutas.Login.ruta,
        Rutas.ConfiguracionMeta.ruta
    )

    val mostrarBarraInferior = rutaActual !in rutasSinBarra

    Scaffold(

        topBar = {
            BarraSuperior()
        },

        bottomBar = {
            if (mostrarBarraInferior) {
                BarraNavegacionInferior(navController)
            }
        }

    ) { padding ->

        GrafoNavegacion(
            navController = navController,
            startDestination = startDestination, // 🔥 CLAVE
            modifier = Modifier.padding(padding)
        )
    }
}