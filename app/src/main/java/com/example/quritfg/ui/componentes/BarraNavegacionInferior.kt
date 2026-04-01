package com.example.quritfg.ui.componentes

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color // 🔥 IMPORTANTE
import com.example.quritfg.R
import com.example.quritfg.ui.navegacion.Rutas

@Composable
fun BarraNavegacionInferior(navController: NavController) {

    val items = listOf(
        Rutas.Inicio,
        Rutas.AnadirGasto,
        Rutas.Progreso,
        Rutas.Historial,
        Rutas.Metas
    )

    NavigationBar {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { ruta ->

            NavigationBarItem(
                selected = currentRoute == ruta.ruta,
                onClick = {
                    navController.navigate(ruta.ruta) {
                        popUpTo(Rutas.Inicio.ruta)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = obtenerIcono(ruta)),
                        contentDescription = ruta.ruta,

                        // 🔥 SOLUCIÓN CLAVE
                        tint = Color.Unspecified
                    )
                }
            )
        }
    }
}

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