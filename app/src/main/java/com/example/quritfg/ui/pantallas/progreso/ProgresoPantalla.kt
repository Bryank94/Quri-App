package com.example.quritfg.ui.pantallas.progreso

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
import com.example.quritfg.datos.modelo.ResumenFinanciero
import com.example.quritfg.ui.viewmodels.ProgresoViewModel
import com.example.quritfg.ui.viewmodels.ProgresoViewModelFactory

/**
 * Pantalla de progreso de la meta.
 *
 * Muestra un analisis mas completo que la pantalla de inicio.
 */
@Composable
fun ProgresoPantalla(navController: NavController) {

    // contexto + repositorio
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    // viewmodel (trae meta + resumen)
    val vm: ProgresoViewModel = viewModel(
        factory = ProgresoViewModelFactory(repositorio)
    )

    // meta actual
    val meta: MetaEntidad? by vm.metaActual.collectAsState(initial = null)

    // resumen financiero
    val resumen by vm.resumenFinanciero.collectAsState(
        initial = ResumenFinanciero(
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
            text = "Progreso",
            style = MaterialTheme.typography.headlineSmall
        )

        /**
         * Si no hay meta, corta aqui
         */
        if (meta == null) {
            Text("No hay una meta configurada todavía.")
            Text("Ve a configuración de meta para crear una.")
            return@Column
        }

        /**
         * Card con todos los datos
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
                    text = "Resumen",
                    style = MaterialTheme.typography.titleMedium
                )

                // datos principales
                Text("Meta: ${meta?.nombre}")
                Text("Objetivo: ${resumen.objetivo}")
                Text("Total ingresos: ${resumen.totalIngresos}")
                Text("Total gastos: ${resumen.totalGastos}")
                Text("Ahorro actual: ${resumen.ahorroActual}")
                Text("Ahorro restante: ${resumen.ahorroRestante}")
            }
        }

        /**
         * Progreso visual
         */
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // convierte a porcentaje
            Text(
                text = "Avance: ${(resumen.porcentajeProgreso * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium
            )

            LinearProgressIndicator(
                progress = resumen.porcentajeProgreso,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // texto informativo
        Text(
            text = "Este es un análisis general. Aquí no se editan datos.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}