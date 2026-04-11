package com.example.quritfg.ui.componentes

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import com.example.quritfg.R
import com.example.quritfg.ui.navegacion.Rutas

/**
 * Barra de navegacion inferior de la app.
 *
 * Permite moverse entre las pantallas principales.
 * Es la tipica barra con iconos abajo.
 */
@Composable
fun BarraNavegacionInferior(navController: NavController) {

    // lista de pantallas que se muestran en la barra
    val items = listOf(
        Rutas.Inicio,
        Rutas.AnadirGasto,
        Rutas.Progreso,
        Rutas.Historial,
        Rutas.Metas
    )

    NavigationBar {

        // detecta en que pantalla estamos ahora
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { ruta ->

            NavigationBarItem(
                // marca el item seleccionado
                selected = currentRoute == ruta.ruta,

                // navegacion entre pantallas
                onClick = {
                    navController.navigate(ruta.ruta) {
                        popUpTo(Rutas.Inicio.ruta) // evita acumular pantallas
                        launchSingleTop = true // evita duplicados
                    }
                },

                icon = {
                    Icon(
                        painter = painterResource(id = obtenerIcono(ruta)),
                        contentDescription = ruta.ruta,

                        // clave: mantiene el color original del icono
                        tint = Color.Unspecified
                    )
                }
            )
        }
    }
}

// Devuelve el icono segun la ruta.

private fun obtenerIcono(ruta: Rutas): Int {
    return when (ruta) {
        Rutas.Inicio -> R.drawable.resumen
        Rutas.AnadirGasto -> R.drawable.anadir
        Rutas.Progreso -> R.drawable.progreso
        Rutas.Historial -> R.drawable.historial
        Rutas.Metas -> R.drawable.metas
        else -> R.drawable.resumen
    }
}