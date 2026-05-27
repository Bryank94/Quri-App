package com.example.quritfg.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.quritfg.R
import com.example.quritfg.ui.navegacion.Rutas

@Composable
fun BarraNavegacionInferior(navController: NavController) {
    val items = listOf(
        Rutas.Inicio,
        Rutas.AnadirGasto,
        Rutas.Metas,
        Rutas.Finanzas,
        Rutas.Progreso
    )

    NavigationBar(
        modifier = Modifier.height(94.dp),
        containerColor = Color(0xEE04180D),
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { ruta ->
            val seleccionado = currentRoute == ruta.ruta

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    navController.navigate(ruta.ruta) {
                        popUpTo(Rutas.Inicio.ruta)
                        launchSingleTop = true
                    }
                },
                icon = {
                    ImagenIcono(
                        ruta = ruta,
                        modifier = Modifier.size(tamanoIcono(ruta, seleccionado)),
                        crop = ruta == Rutas.AnadirGasto || ruta == Rutas.Progreso || ruta == Rutas.Metas,
                        recortar = true
                    )
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private fun tamanoIcono(ruta: Rutas, seleccionado: Boolean) =
    when (ruta) {
        Rutas.Inicio -> if (seleccionado) 60.dp else 56.dp
        Rutas.AnadirGasto -> if (seleccionado) 70.dp else 65.dp
        Rutas.Metas -> if (seleccionado) 88.dp else 81.dp
        Rutas.Finanzas -> if (seleccionado) 51.dp else 51.dp
        Rutas.Progreso -> if (seleccionado) 67.dp else 63.dp
        else -> if (seleccionado) 63.dp else 58.dp
    }

@Composable
private fun ImagenIcono(
    ruta: Rutas,
    modifier: Modifier,
    crop: Boolean,
    recortar: Boolean
) {
    Image(
        painter = painterResource(id = obtenerIcono(ruta)),
        contentDescription = ruta.ruta,
        modifier = if (recortar) modifier.clip(RoundedCornerShape(10.dp)) else modifier,
        contentScale = if (crop) ContentScale.Crop else ContentScale.Fit
    )
}

private fun obtenerIcono(ruta: Rutas): Int =
    when (ruta) {
        Rutas.Inicio -> R.drawable.resumen
        Rutas.AnadirGasto -> R.drawable.anadir
        Rutas.Progreso -> R.drawable.progreso
        Rutas.Historial -> R.drawable.historial
        Rutas.Metas -> R.drawable.metas
        Rutas.Banco -> R.drawable.historial
        Rutas.PlanMensual -> R.drawable.progreso
        Rutas.Finanzas -> R.drawable.historial
        Rutas.Perfil -> R.drawable.quri_profile
        else -> R.drawable.resumen
    }

