package com.example.quritfg.ui.pantallas.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.config.quriValor
import com.example.quritfg.ui.componentes.ConsejoQuri
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.FilaDatoQuri
import com.example.quritfg.ui.componentes.IconoCircularQuri
import com.example.quritfg.ui.componentes.LeyendaColorQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.modelo.HistorialMes
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.RojoPeligro
import com.example.quritfg.ui.theme.VerdeDinero
import com.example.quritfg.ui.viewmodels.HistorialViewModel
import com.example.quritfg.ui.viewmodels.HistorialViewModelFactory
import java.util.Locale
import kotlin.math.abs
import java.math.BigDecimal
import java.math.RoundingMode

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
    val ingresos by vm.ingresos.collectAsState(initial = emptyList())
    val consejo by vm.consejoAhorro.collectAsState(
        initial = "Registra tus gastos para empezar a recibir consejos."
    )

    var ingresoEditando by remember { mutableStateOf<IngresoEntidad?>(null) }
    var ingresoEliminando by remember { mutableStateOf<IngresoEntidad?>(null) }
    var gastoEditando by remember { mutableStateOf<GastoEntidad?>(null) }
    var gastoEliminando by remember { mutableStateOf<GastoEntidad?>(null) }

    /**
     * Si no hay datos, muestra mensaje simple
     */
    if (historial.isEmpty() && ingresos.isEmpty()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quriTexto("No hay movimientos registrados", "No movements registered"),
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 112.dp)
        ) {

            item {
                TituloPantallaQuri(quriTexto("Historial", "History"))
            }

            item {
                ConsejoAhorro(consejo)
            }

            if (ingresos.isNotEmpty()) {
                item {
                    TarjetaQuri {
                        EncabezadoTarjetaQuri("UP", quriTexto("Ingresos registrados", "Registered income"))
                        ingresos.forEach { ingreso ->
                            ItemIngreso(
                                ingreso = ingreso,
                                onEditar = { ingresoEditando = ingreso },
                                onEliminar = { ingresoEliminando = ingreso }
                            )
                            SeparadorQuri()
                        }
                    }
                }
            }

            if (historial.isNotEmpty()) {
                item {
                    GraficoComparativoMensual(historial.take(6).reversed())
                }
            }

            // recorre cada mes
            items(historial) { bloqueMes ->

                EncabezadoMes(bloqueMes)

                // lista de gastos dentro del mes
                bloqueMes.gastos.forEach { gasto ->
                    ItemGasto(gasto, onEditar = { gastoEditando = gasto }, onEliminar = { gastoEliminando = gasto })
                }
            }
        }
    }
    ingresoEditando?.let { ingreso ->
        DialogoEditarIngreso(
            ingreso = ingreso,
            onCerrar = { ingresoEditando = null },
            onGuardar = { actualizado ->
                vm.actualizarIngreso(actualizado)
                ingresoEditando = null
            }
        )
    }

    ingresoEliminando?.let { ingreso ->
        DialogoConfirmarEliminacion(
            titulo = quriTexto("Eliminar ingreso", "Delete income"),
            mensaje = quriTexto("Se eliminara este ingreso del historial.", "This income will be deleted from history."),
            onCerrar = { ingresoEliminando = null },
            onConfirmar = {
                vm.eliminarIngreso(ingreso.id)
                ingresoEliminando = null
            }
        )
    }

    gastoEditando?.let { gasto ->
        DialogoEditarGasto(
            gasto = gasto,
            onCerrar = { gastoEditando = null },
            onGuardar = { actualizado ->
                vm.actualizarGasto(actualizado)
                gastoEditando = null
            }
        )
    }

    gastoEliminando?.let { gasto ->
        DialogoConfirmarEliminacion(
            titulo = quriTexto("Eliminar gasto", "Delete expense"),
            mensaje = quriTexto("Se eliminara este gasto del historial.", "This expense will be deleted from history."),
            onCerrar = { gastoEliminando = null },
            onConfirmar = {
                vm.eliminarGasto(gasto.id)
                gastoEliminando = null
            }
        )
    }
}

@Composable
fun ConsejoAhorro(consejo: String) {
    ConsejoQuri(
        icono = "!",
        titulo = quriTexto("Consejo de ahorro", "Savings tip"),
        texto = quriValor(consejo)
    )
}

@Composable
fun GraficoComparativoMensual(historial: List<HistorialMes>) {
    val maximo = historial.maxOfOrNull { it.totalGastadoCentimos }?.coerceAtLeast(1L) ?: 1L
    val mesActual = historial.lastOrNull()
    val totalNecesario = mesActual?.let { (it.totalGastadoCentimos - it.totalInnecesarioCentimos).coerceAtLeast(0L) } ?: 0L
    val totalInnecesario = mesActual?.totalInnecesarioCentimos ?: 0L

    TarjetaQuri {
        EncabezadoTarjetaQuri(icono = "%", titulo = quriTexto("Comparativa mensual", "Monthly comparison"))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val espacio = 16.dp.toPx()
                val anchoBarra = ((size.width - espacio * (historial.size + 1)) / historial.size.coerceAtLeast(1))
                    .coerceAtLeast(12.dp.toPx())
                val altoMaximo = size.height - 28.dp.toPx()

                historial.forEachIndexed { index, mes ->
                    val proporcion = (mes.totalGastadoCentimos.toDouble() / maximo.toDouble()).toFloat().coerceIn(0.04f, 1f)
                    val alto = altoMaximo * proporcion
                    val x = espacio + index * (anchoBarra + espacio)
                    val y = altoMaximo - alto
                    val color = when {
                        proporcion >= 0.75f -> RojoPeligro
                        proporcion >= 0.45f -> DoradoDinero
                        else -> VerdeDinero
                    }

                    drawRoundRect(
                        color = color.copy(alpha = 0.18f),
                        topLeft = Offset(x, 0f),
                        size = Size(anchoBarra, altoMaximo),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(anchoBarra, alto),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
            }

            historial.forEach { mes ->
                val proporcion = (mes.totalGastadoCentimos.toDouble() / maximo.toDouble()).toFloat().coerceIn(0.05f, 1f)
                val color = when {
                    proporcion >= 0.75f -> RojoPeligro
                    proporcion >= 0.45f -> DoradoDinero
                    else -> VerdeDinero
                }

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
                                    color = color,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                    }

                    Text(
                        text = formatearDineroQuri(mes.totalGastadoCentimos),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(78.dp)
                    )
                }
            }

            if (mesActual != null) {
                SeparadorQuri()
                Text(quriTexto("Detalle del ultimo mes", "Last month detail"), color = DoradoDinero, style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GraficoCircularGastos(
                        necesarioCentimos = totalNecesario,
                        innecesarioCentimos = totalInnecesario,
                        modifier = Modifier.size(128.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        LeyendaColorQuri(VerdeDinero, quriTexto("Necesario", "Necessary"), formatearDineroQuri(totalNecesario))
                        LeyendaColorQuri(RojoPeligro, quriTexto("Innecesario", "Unnecessary"), formatearDineroQuri(totalInnecesario))
                        Text(
                            text = if (totalInnecesario > 0L) {
                                quriTexto("El rojo marca dinero que puedes recuperar para tus fondos.", "Red marks money you can recover for your funds.")
                            } else {
                                quriTexto("Sin gasto innecesario registrado este mes.", "No unnecessary expense registered this month.")
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
    }
}

@Composable
private fun GraficoCircularGastos(
    necesarioCentimos: Long,
    innecesarioCentimos: Long,
    modifier: Modifier = Modifier
) {
    val total = (necesarioCentimos + innecesarioCentimos).coerceAtLeast(1L)
    val anguloInnecesario = (innecesarioCentimos.toFloat() / total.toFloat()) * 360f

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 18.dp.toPx())
        val margen = 12.dp.toPx()
        val area = Size(size.width - margen * 2, size.height - margen * 2)
        val topLeft = Offset(margen, margen)

        drawArc(
            color = VerdeDinero,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = area,
            style = stroke
        )
        drawArc(
            color = RojoPeligro,
            startAngle = -90f,
            sweepAngle = anguloInnecesario,
            useCenter = false,
            topLeft = topLeft,
            size = area,
            style = stroke
        )
    }
}

/**
 * Item individual de gasto
 */
@Composable
fun ItemGasto(
    gasto: GastoEntidad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    TarjetaQuri {
        FilaDatoQuri(
            icono = if (gasto.etiqueta == "Innecesario") "DN" else "$",
            etiqueta = quriValor(gasto.categoria),
            valor = formatearDineroQuri(gasto.cantidadCentimos),
            valorColor = if (gasto.etiqueta == "Innecesario") RojoPeligro else DoradoDinero,
            detalle = gasto.fecha
        )
        Text(
            text = quriValor(gasto.etiqueta),
            color = Color.White.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodySmall
        )
        AccionesMovimiento(onEditar = onEditar, onEliminar = onEliminar)
    }
}

@Composable
fun ItemIngreso(
    ingreso: IngresoEntidad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    FilaDatoQuri(
        icono = "UP",
        etiqueta = quriValor(ingreso.concepto ?: quriTexto("Ingreso", "Income")),
        valor = formatearDineroQuri(ingreso.cantidadCentimos),
        valorColor = VerdeDinero,
        detalle = ingreso.fecha
    )
    AccionesMovimiento(onEditar = onEditar, onEliminar = onEliminar)
}

@Composable
private fun AccionesMovimiento(
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onEditar) {
            Text(quriTexto("Editar", "Edit"), color = DoradoDinero)
        }
        TextButton(onClick = onEliminar) {
            Text(quriTexto("Eliminar", "Delete"), color = RojoPeligro)
        }
    }
}

@Composable
private fun DialogoEditarIngreso(
    ingreso: IngresoEntidad,
    onCerrar: () -> Unit,
    onGuardar: (IngresoEntidad) -> Unit
) {
    var concepto by remember(ingreso.id) { mutableStateOf(ingreso.concepto.orEmpty()) }
    var cantidad by remember(ingreso.id) { mutableStateOf(centimosAEntrada(ingreso.cantidadCentimos)) }
    var fecha by remember(ingreso.id) { mutableStateOf(ingreso.fecha) }
    var error by remember { mutableStateOf<String?>(null) }
    val errorFormulario = quriTexto("Revisa cantidad y fecha", "Check amount and date")

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            Button(onClick = {
                val centimos = textoACentimos(cantidad)
                if (centimos == null || centimos <= 0L || !fechaValida(fecha)) {
                    error = errorFormulario
                } else {
                    onGuardar(ingreso.copy(cantidadCentimos = centimos, fecha = fecha.trim(), concepto = concepto.trim().ifBlank { null }))
                }
            }) { Text(quriTexto("Guardar", "Save")) }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text(quriTexto("Cancelar", "Cancel")) } },
        title = { Text(quriTexto("Editar ingreso", "Edit income")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = concepto, onValueChange = { concepto = it }, label = { Text(quriTexto("Concepto", "Concept")) })
                OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text(quriTexto("Cantidad", "Amount")) })
                OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text(quriTexto("Fecha dd-mm-yyyy", "Date dd-mm-yyyy")) })
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        containerColor = Color(0xFF06160D),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun DialogoEditarGasto(
    gasto: GastoEntidad,
    onCerrar: () -> Unit,
    onGuardar: (GastoEntidad) -> Unit
) {
    var categoria by remember(gasto.id) { mutableStateOf(gasto.categoria) }
    var cantidad by remember(gasto.id) { mutableStateOf(centimosAEntrada(gasto.cantidadCentimos)) }
    var fecha by remember(gasto.id) { mutableStateOf(gasto.fecha) }
    var etiqueta by remember(gasto.id) { mutableStateOf(gasto.etiqueta) }
    var error by remember { mutableStateOf<String?>(null) }
    val errorFormulario = quriTexto("Revisa categoria, cantidad y fecha", "Check category, amount and date")

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            Button(onClick = {
                val centimos = textoACentimos(cantidad)
                if (categoria.isBlank() || centimos == null || centimos <= 0L || !fechaValida(fecha)) {
                    error = errorFormulario
                } else {
                    onGuardar(gasto.copy(categoria = categoria.trim(), cantidadCentimos = centimos, fecha = fecha.trim(), etiqueta = etiqueta.trim().ifBlank { "Necesario" }))
                }
            }) { Text(quriTexto("Guardar", "Save")) }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text(quriTexto("Cancelar", "Cancel")) } },
        title = { Text(quriTexto("Editar gasto", "Edit expense")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text(quriTexto("Categoria", "Category")) })
                OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text(quriTexto("Cantidad", "Amount")) })
                OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text(quriTexto("Fecha dd-mm-yyyy", "Date dd-mm-yyyy")) })
                OutlinedTextField(value = etiqueta, onValueChange = { etiqueta = it }, label = { Text(quriTexto("Etiqueta", "Tag")) })
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        containerColor = Color(0xFF06160D),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun DialogoConfirmarEliminacion(
    titulo: String,
    mensaje: String,
    onCerrar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            Button(onClick = onConfirmar, colors = ButtonDefaults.buttonColors(containerColor = RojoPeligro)) {
                Text(quriTexto("Eliminar", "Delete"))
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text(quriTexto("Cancelar", "Cancel")) } },
        title = { Text(titulo) },
        text = { Text(mensaje) },
        containerColor = Color(0xFF06160D),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

private fun fechaValida(fecha: String): Boolean =
    runCatching { FechaQuri.parsear(fecha) }.isSuccess

private fun centimosAEntrada(centimos: Long): String =
    BigDecimal(centimos).divide(BigDecimal(100), 2, RoundingMode.HALF_UP).toPlainString().replace('.', ',')
/**
 * Encabezado para cada mes
 */
@Composable
fun EncabezadoMes(mes: HistorialMes) {

    TarjetaQuri {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoCircularQuri(icono = "CAL", size = 42)
            Column {
                Text(
                    text = mes.mes,
                    color = DoradoDinero,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                val comparacion = mes.comparacionMesAnterior
                if (comparacion != null) {
                    Text(
                        text = "${quriTexto("Total", "Total")}: ${formatearDineroQuri(mes.totalGastadoCentimos)} - ${formatearPorcentaje(abs(comparacion))}% ${if (comparacion < 0) quriTexto("menos", "less") else quriTexto("mas", "more")} ${quriTexto("que el mes anterior", "than previous month")}",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "${quriTexto("Total", "Total")}: ${formatearDineroQuri(mes.totalGastadoCentimos)}",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun formatearPorcentaje(valor: Double): String =
    String.format(Locale.US, "%.2f", valor)






