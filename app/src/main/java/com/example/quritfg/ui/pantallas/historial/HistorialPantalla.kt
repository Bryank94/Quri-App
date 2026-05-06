package com.example.quritfg.ui.pantallas.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.ui.modelo.HistorialMes
import com.example.quritfg.ui.viewmodels.HistorialViewModel
import com.example.quritfg.ui.viewmodels.HistorialViewModelFactory
import java.util.Locale
import kotlin.math.abs

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
    val consejo by vm.consejoAhorro.collectAsState(
        initial = "Registra tus gastos para empezar a recibir consejos."
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

            item {
                ConsejoAhorro(consejo)
            }

            item {
                GraficoComparativoMensual(historial.take(6).reversed())
            }

            // recorre cada mes
            items(historial) { bloqueMes ->

                EncabezadoMes(bloqueMes)

                // lista de gastos dentro del mes
                bloqueMes.gastos.forEach { gasto ->
                    ItemGasto(gasto)
                }
            }
        }
    }
}

@Composable
fun ConsejoAhorro(consejo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = consejo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun GraficoComparativoMensual(historial: List<HistorialMes>) {
    val maximo = historial.maxOfOrNull { it.totalGastado }?.coerceAtLeast(1.0) ?: 1.0

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Comparativa mensual",
                style = MaterialTheme.typography.titleMedium
            )

            historial.forEach { mes ->
                val proporcion = (mes.totalGastado / maximo).toFloat().coerceIn(0.05f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = mes.mes,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(58.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(proporcion)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                    }

                    Text(
                        text = "${formatearEuros(mes.totalGastado)} EUR",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(78.dp)
                    )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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

            AssistChip(
                onClick = {},
                label = { Text(gasto.etiqueta) }
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
fun EncabezadoMes(mes: HistorialMes) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp
            )
        ) {
            Text(
                text = mes.mes,
                style = MaterialTheme.typography.titleMedium
            )

            val comparacion = mes.comparacionMesAnterior
            if (comparacion != null) {
                val direccion = if (comparacion < 0) "menos" else "mas"
                Text(
                    text = "Total: ${formatearEuros(mes.totalGastado)} EUR - ${formatearEuros(abs(comparacion))}% $direccion que el mes anterior",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Total: ${formatearEuros(mes.totalGastado)} EUR",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatearEuros(valor: Double): String =
    String.format(Locale.US, "%.2f", valor)
