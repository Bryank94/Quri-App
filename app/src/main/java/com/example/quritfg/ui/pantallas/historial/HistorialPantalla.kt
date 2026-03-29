package com.example.quritfg.ui.pantallas.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.ui.modelo.HistorialMes
import com.example.quritfg.ui.viewmodels.HistorialViewModel
import com.example.quritfg.ui.viewmodels.HistorialViewModelFactory

/**
 * Pantalla que muestra el historial de gastos agrupado por mes.
 */
@Composable
fun HistorialPantalla(navController: NavController) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: HistorialViewModel = viewModel(
        factory = HistorialViewModelFactory(repositorio)
    )

    // Recoge el historial agrupado desde el ViewModel
    val historial by vm.historialAgrupado.collectAsState(
        initial = emptyList()
    )

    if (historial.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay gastos registrados")
        }

    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(historial) { bloqueMes ->

                // Encabezado del mes
                EncabezadoMes(bloqueMes.mes)

                // Lista de gastos del mes
                bloqueMes.gastos.forEach { gasto ->
                    ItemGasto(gasto)
                }
            }
        }
    }
}

/**
 * Composable que muestra un gasto individual.
 */
@Composable
fun ItemGasto(gasto: GastoEntidad) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = gasto.categoria,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cantidad: ${gasto.cantidad}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Fecha: ${gasto.fecha}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Encabezado visual para cada mes del historial.
 */
@Composable
fun EncabezadoMes(mes: String) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Text(
            text = mes,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            )
        )
    }
}