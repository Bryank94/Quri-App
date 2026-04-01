package com.example.quritfg.ui.pantallas.metas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun MetasPantalla(navController: NavController) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: MetasViewModel = viewModel(
        factory = MetasViewModelFactory(repositorio)
    )

    val meta: MetaEntidad? by vm.metaActual.collectAsState(initial = null)

    val metaLocal = meta

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Metas",
            style = MaterialTheme.typography.headlineSmall
        )

        if (metaLocal == null) {
            Text("No tienes ninguna meta configurada todavía.")
        } else {

            Card(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Meta actual",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("Nombre: ${metaLocal.nombre}")
                    Text("Objetivo: ${metaLocal.cantidadObjetivo}")
                    Text("Actual: ${metaLocal.cantidadActual}")
                }
            }
        }

        Button(
            onClick = {
                navController.navigate(Rutas.ConfiguracionMeta.ruta) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            // 🔥 NUEVO
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Nueva meta")
        }
    }
}