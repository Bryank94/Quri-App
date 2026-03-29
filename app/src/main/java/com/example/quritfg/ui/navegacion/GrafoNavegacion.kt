package com.example.quritfg.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quritfg.ui.pantallas.anadir.AnadirGastoPantalla
import com.example.quritfg.ui.pantallas.configuracion.ConfiguracionMetaPantalla
import com.example.quritfg.ui.pantallas.historial.HistorialPantalla
import com.example.quritfg.ui.pantallas.inicio.InicioPantalla
import com.example.quritfg.ui.pantallas.metas.MetasPantalla
import com.example.quritfg.ui.pantallas.progreso.ProgresoPantalla
import com.example.quritfg.ui.pantallas.registro.RegistroPantalla

/**
 * Define todas las pantallas de la aplicacion y
 * como se navega entre ellas.
 */
@Composable
fun GrafoNavegacion(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Rutas.Registro.ruta,
        modifier = modifier
    ) {

        // Pantalla inicial de la aplicacion
        composable(Rutas.Registro.ruta) {
            RegistroPantalla(navController)
        }

        // Configuracion inicial de la meta
        composable(Rutas.ConfiguracionMeta.ruta) {
            ConfiguracionMetaPantalla(
                navController,
                esPrimeraConfiguracion = true
            )
        }

        // Pantallas principales
        composable(Rutas.Inicio.ruta) {
            InicioPantalla(navController)
        }

        composable(Rutas.AnadirGasto.ruta) {
            AnadirGastoPantalla(navController)
        }

        composable(Rutas.Progreso.ruta) {
            ProgresoPantalla(navController)
        }

        composable(Rutas.Historial.ruta) {
            HistorialPantalla(navController)
        }

        composable(Rutas.Metas.ruta) {
            MetasPantalla(navController)
        }
    }
}