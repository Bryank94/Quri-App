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

@Composable
fun ProgresoPantalla(navController: NavController) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: ProgresoViewModel = viewModel(
        factory = ProgresoViewModelFactory(repositorio)
    )

    val meta: MetaEntidad? by vm.metaActual.collectAsState(initial = null)

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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Progreso", style = MaterialTheme.typography.headlineMedium)

        if (meta == null) {
            Text("No hay una meta configurada todavía.")
            Text("Ve a configuración de meta para crear una.")
            return@Column
        }

        Text("Meta: ${meta?.nombre}")
        Text("Objetivo: ${resumen.objetivo}")
        Text("Total ingresos: ${resumen.totalIngresos}")
        Text("Total gastos: ${resumen.totalGastos}")
        Text("Ahorro actual: ${resumen.ahorroActual}")
        Text("Ahorro restante: ${resumen.ahorroRestante}")

        Spacer(Modifier.height(8.dp))

        Text("Avance: ${(resumen.porcentajeProgreso * 100).toInt()}%")

        LinearProgressIndicator(
            progress = resumen.porcentajeProgreso,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Este es un análisis general. Aquí no se editan datos.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}