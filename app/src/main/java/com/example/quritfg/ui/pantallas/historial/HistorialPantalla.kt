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
import com.example.quritfg.ui.viewmodels.HistorialViewModel
import com.example.quritfg.ui.viewmodels.HistorialViewModelFactory

/**
 * Pantalla de historial de gastos.
 *
 * Muestra los gastos agrupados por mes.
 */
@Composable
fun HistorialPantalla(navController: NavController) {

    // contexto + repositorio
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    // viewmodel (aqui viene ya el historial preparado)
    val vm: HistorialViewModel = viewModel(
        factory = HistorialViewModelFactory(repositorio)
    )

    // estado observado desde el viewmodel
    val historial by vm.historialAgrupado.collectAsState(
        initial = emptyList()
    )

    /**
     * Si no hay datos, muestra mensaje simple
     */
    if (historial.isEmpty()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay gastos registrados",
                style = MaterialTheme.typography.bodyMedium
            )
        }

    } else {

        /**
         * Lista principal con meses + gastos
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // recorre cada mes
            items(historial) { bloqueMes ->

                EncabezadoMes(bloqueMes.mes)

                // lista de gastos dentro del mes
                bloqueMes.gastos.forEach { gasto ->
                    ItemGasto(gasto)
                }
            }
        }
    }
}

/**
 * Item individual de gasto
 */
@Composable
fun ItemGasto(gasto: GastoEntidad) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // un poco de profundidad
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // categoria
            Text(
                text = gasto.categoria,
                style = MaterialTheme.typography.titleMedium
            )

            // cantidad
            Text(
                text = "Cantidad: ${gasto.cantidad}",
                style = MaterialTheme.typography.bodyMedium
            )

            // fecha
            Text(
                text = "Fecha: ${gasto.fecha}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Encabezado para cada mes
 */
@Composable
fun EncabezadoMes(mes: String) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp // mejor que usar color fijo
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