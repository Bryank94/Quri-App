package com.example.quritfg.ui.pantallas.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.componentes.colorProgreso
import com.example.quritfg.ui.viewmodels.InicioViewModel
import com.example.quritfg.ui.viewmodels.InicioViewModelFactory

@Composable
fun InicioPantalla(navController: NavController) {

    // contexto + repositorio
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    // viewmodel (trae meta + resumen ya calculado)
    val vm: InicioViewModel = viewModel(
        factory = InicioViewModelFactory(repositorio)
    )

    // estado de la meta (puede ser null)
    val meta by vm.metaActual.collectAsState(initial = null)

    // resumen financiero completo
    val resumen by vm.resumenFinanciero.collectAsState(
        initial = com.example.quritfg.datos.modelo.ResumenFinanciero(
            totalIngresos = 0.0,
            totalGastos = 0.0,
            ahorroActual = 0.0,
            objetivo = 0.0,
            porcentajeProgreso = 0f,
            ahorroRestante = 0.0
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Resumen",
            style = MaterialTheme.typography.headlineSmall
        )

        /**
         * Card con toda la info
         */
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Meta actual",
                    style = MaterialTheme.typography.titleMedium
                )

                // datos principales (vienen del viewmodel)
                Text("Nombre: ${meta?.nombre ?: "Sin meta"}")
                Text("Objetivo: ${resumen.objetivo}")

                Text("Total ingresos: ${resumen.totalIngresos}")
                Text("Total gastos: ${resumen.totalGastos}")
                Text("Ahorro actual: ${resumen.ahorroActual}")
            }
        }

        /**
         * Barra de progreso de la meta
         */
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(
                text = "Progreso",
                style = MaterialTheme.typography.titleMedium
            )

            LinearProgressIndicator(
                progress = resumen.porcentajeProgreso,
                color = colorProgreso(resumen.porcentajeProgreso),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
