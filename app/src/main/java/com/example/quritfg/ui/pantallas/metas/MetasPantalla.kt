package com.example.quritfg.ui.pantallas.metas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.MetasViewModel
import com.example.quritfg.ui.viewmodels.MetasViewModelFactory

/**
 * Pantalla que muestra la meta actual almacenada en la base de datos.
 */
@Composable
fun MetasPantalla(navController: NavController) {

    // Se obtiene el repositorio y se inyecta en el ViewModel
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: MetasViewModel = viewModel(
        factory = MetasViewModelFactory(repositorio)
    )

    // Se recoge la meta desde Room
    val meta: MetaEntidad? by vm.metaActual.collectAsState(initial = null)

    val metaLocal = meta

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Metas", style = MaterialTheme.typography.headlineMedium)

        // Si no existe meta, se informa al usuario
        if (metaLocal == null) {
            Text("No tienes ninguna meta configurada todavía.")
        } else {
            // Si existe, se muestra en una tarjeta
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Meta actual", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Nombre: ${metaLocal.nombre}")
                    Text("Objetivo: ${metaLocal.cantidadObjetivo}")
                    Text("Actual: ${metaLocal.cantidadActual}")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Boton que navega a la pantalla de configuracion
        Button(
            onClick = {
                navController.navigate(Rutas.ConfiguracionMeta.ruta) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nueva meta")
        }
    }
}