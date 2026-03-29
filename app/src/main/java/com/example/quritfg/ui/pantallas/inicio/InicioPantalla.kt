package com.example.quritfg.ui.pantallas.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.viewmodels.InicioViewModel
import com.example.quritfg.ui.viewmodels.InicioViewModelFactory

@Composable
fun InicioPantalla(navController: NavController) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: InicioViewModel = viewModel(
        factory = InicioViewModelFactory(repositorio)
    )

    val meta by vm.metaActual.collectAsState(initial = null)
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Resumen", style = MaterialTheme.typography.headlineMedium)

        Text("Meta actual: ${meta?.nombre ?: "Sin meta"}")
        Text("Objetivo: ${resumen.objetivo}")

        Text("Total ingresos: ${resumen.totalIngresos}")
        Text("Total gastos: ${resumen.totalGastos}")
        Text("Ahorro actual: ${resumen.ahorroActual}")

        LinearProgressIndicator(
            progress = resumen.porcentajeProgreso,
            modifier = Modifier.fillMaxWidth()
        )
    }
}