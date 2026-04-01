package com.example.quritfg.ui.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quritfg.ui.componentes.BarraNavegacionInferior
import com.example.quritfg.ui.componentes.BarraSuperior

@Composable
fun QuriApp() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val rutasSinBarra = setOf(
        Rutas.Registro.ruta,
        Rutas.ConfiguracionMeta.ruta
    )

    val mostrarBarraInferior = rutaActual !in rutasSinBarra

    Scaffold(

        // 🔥 AÑADIMOS LA BARRA SUPERIOR AQUÍ
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
            modifier = Modifier.padding(padding)
        )
    }
}