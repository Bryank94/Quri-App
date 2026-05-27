package com.example.quritfg.ui.pantallas.metas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.centimosAEuros
import com.example.quritfg.ui.componentes.BarraProgresoLineaQuri
import com.example.quritfg.ui.componentes.ConsejoLineaQuri
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.FilaDatoQuri
import com.example.quritfg.ui.componentes.SeparadorQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.componentes.colorDineroAlerta
import com.example.quritfg.ui.componentes.colorDineroPositivo
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.RojoPeligro
import com.example.quritfg.ui.viewmodels.MetasViewModel
import com.example.quritfg.ui.viewmodels.MetasViewModelFactory
import java.time.temporal.ChronoUnit

@Composable
fun MetasPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val vm: MetasViewModel = viewModel(
        factory = MetasViewModelFactory(repositorio)
    )

    val fondos: List<MetaEntidad> by vm.fondos.collectAsState(initial = emptyList())
    val gastos: List<GastoEntidad> by vm.gastos.collectAsState(initial = emptyList())
    val categoriaInnecesaria = categoriaInnecesariaPrincipal(gastos)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantallaQuri(quriTexto("Fondos", "Funds"))

        Button(
            onClick = {
                navController.navigate(Rutas.ConfiguracionMeta.ruta) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DoradoDinero,
                contentColor = Color(0xFF06160D)
            )
        ) {
            Text(quriTexto("Nuevo fondo", "New fund"), fontWeight = FontWeight.Bold)
        }

        if (fondos.isEmpty()) {
            TarjetaQuri {
                EncabezadoTarjetaQuri(icono = "+", titulo = quriTexto("Sin fondos", "No funds"))
                Text(quriTexto("Crea tu primer objetivo para empezar a repartir ingresos.", "Create your first goal to start splitting income."), color = Color.White)
            }
        } else {
            fondos.forEach { fondo ->
                FondoCard(
                    fondo = fondo,
                    categoriaInnecesaria = categoriaInnecesaria,
                    onGuardar = { nombre, objetivo, fechaLimite, prioridad ->
                        vm.actualizarFondo(fondo, nombre, objetivo, fechaLimite, prioridad)
                    },
                    onEliminar = { vm.eliminarFondo(fondo) }
                )
            }
        }
    }
}

@Composable
private fun FondoCard(
    fondo: MetaEntidad,
    categoriaInnecesaria: String?,
    onGuardar: (String, String, String, Int) -> Boolean,
    onEliminar: () -> Unit
) {
    var editando by remember(fondo.id) { mutableStateOf(false) }
    var expandido by remember(fondo.id) { mutableStateOf(false) }
    var mostrarConfirmacionBorrado by remember(fondo.id) { mutableStateOf(false) }
    var nombre by remember(fondo.id, fondo.nombre) { mutableStateOf(fondo.nombre) }
    var objetivo by remember(fondo.id, fondo.cantidadObjetivoCentimos) {
        mutableStateOf(centimosAEuros(fondo.cantidadObjetivoCentimos).toPlainString())
    }
    var fechaLimite by remember(fondo.id, fondo.fechaLimite) { mutableStateOf(fondo.fechaLimite) }
    var prioridad by remember(fondo.id, fondo.prioridad) { mutableStateOf(fondo.prioridad) }
    var error by remember(fondo.id) { mutableStateOf<String?>(null) }

    val porcentaje = if (fondo.cantidadObjetivoCentimos > 0L) {
        (fondo.cantidadActualCentimos.toDouble() / fondo.cantidadObjetivoCentimos.toDouble())
            .toFloat()
            .coerceIn(0f, 1f)
    } else {
        0f
    }
    val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
    val diasRestantes = diasRestantes(fondo.fechaLimite)
    val porcentajeTexto = (porcentaje * 100).toInt()
    val progresoTiempo = progresoTiempo(fondo.fechaLimite)
    val consejo = categoriaInnecesaria?.let {
        quriTexto("Si dejas de gastar en $it innecesario, te acercaras mas a llegar a tu objetivo.", "If you stop spending on unnecessary $it, you will get closer to your goal.")
    } ?: quriTexto("Etiqueta tus gastos innecesarios para saber donde puedes recortar y acercarte mas a tu objetivo.", "Tag unnecessary expenses to know where you can reduce and get closer to your goal.")

    if (mostrarConfirmacionBorrado) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionBorrado = false },
            title = { Text(quriTexto("Eliminar fondo", "Delete fund")) },
            text = { Text(quriTexto("Se eliminara ${fondo.nombre}. Esta accion no se puede deshacer.", "${fondo.nombre} will be deleted. This action cannot be undone.")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionBorrado = false
                        onEliminar()
                    }
                ) { Text(quriTexto("Eliminar", "Delete"), color = RojoPeligro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionBorrado = false }) {
                    Text(quriTexto("Cancelar", "Cancel"))
                }
            }
        )
    }

    TarjetaQuri {
        if (editando) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = null
                },
                label = { Text(quriTexto("Nombre del fondo", "Fund name")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Text(quriTexto("Prioridad", "Priority"), color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    1 to quriTexto("Alta", "High"),
                    2 to quriTexto("Media", "Medium"),
                    3 to quriTexto("Baja", "Low")
                ).forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = prioridad == valor,
                        onClick = { prioridad = valor },
                        label = { Text(etiqueta) },
                        border = BorderStroke(1.dp, DoradoDinero)
                    )
                }
            }

            OutlinedTextField(
                value = objetivo,
                onValueChange = {
                    objetivo = it
                    error = null
                },
                label = { Text(quriTexto("Objetivo", "Goal")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = fechaLimite,
                onValueChange = {
                    fechaLimite = it
                    error = null
                },
                label = { Text(quriTexto("Fecha limite (dd-MM-yyyy)", "Deadline (dd-MM-yyyy)")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val errorFormulario = quriTexto("Introduce nombre, objetivo y fecha limite validos", "Enter a valid name, goal and deadline")
                Button(
                    onClick = {
                        val guardado = onGuardar(nombre, objetivo, fechaLimite, prioridad)
                        if (guardado) editando = false else error = errorFormulario
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(quriTexto("Guardar", "Save"))
                }

                TextButton(
                    onClick = {
                        nombre = fondo.nombre
                        objetivo = centimosAEuros(fondo.cantidadObjetivoCentimos).toPlainString()
                        fechaLimite = fondo.fechaLimite
                        prioridad = fondo.prioridad
                        error = null
                        editando = false
                    }
                ) {
                    Text(quriTexto("Cancelar", "Cancel"))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EncabezadoTarjetaQuri(icono = "SAFE", titulo = fondo.nombre)
                TextButton(onClick = { expandido = !expandido }) {
                    Text(if (expandido) quriTexto("Ocultar", "Hide") else quriTexto("Ver", "View"))
                }
            }

            Text(
                text = "${formatearDineroQuri(fondo.cantidadActualCentimos)} ${quriTexto("de", "of")} ${formatearDineroQuri(fondo.cantidadObjetivoCentimos)}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            BarraProgresoLineaQuri(
                titulo = quriTexto("Avance", "Progress"),
                porcentaje = porcentaje,
                valorDerecha = "$porcentajeTexto%"
            )

            if (expandido) {
                SeparadorQuri()
                FilaDatoQuri("PIG", quriTexto("Restante", "Remaining"), formatearDineroQuri(restante), colorDineroAlerta())
                SeparadorQuri()
                FilaDatoQuri("CAL", quriTexto("Fecha limite", "Deadline"), FechaQuri.mostrar(fondo.fechaLimite))
                SeparadorQuri()
                FilaDatoQuri("!", quriTexto("Prioridad", "Priority"), textoPrioridad(fondo.prioridad), DoradoDinero)
                SeparadorQuri()
                FilaDatoQuri("CLK", quriTexto("Dias restantes", "Days left"), diasRestantes?.toString() ?: quriTexto("Fecha no valida", "Invalid date"), DoradoDinero)
                BarraProgresoLineaQuri(
                    titulo = quriTexto("Tiempo consumido", "Time elapsed"),
                    porcentaje = progresoTiempo,
                    valorDerecha = "${diasRestantes?.toString() ?: "?"} dias"
                )
                ConsejoLineaQuri(
                    icono = "*",
                    titulo = estadoFondo(fondo),
                    texto = if (porcentaje < progresoTiempo) {
                        quriTexto("Vas tarde: necesitas ${formatearDineroQuri(necesarioMensual(fondo))} al mes. $consejo", "You are late: you need ${formatearDineroQuri(necesarioMensual(fondo))} per month. $consejo")
                    } else {
                        quriTexto("Vas bien: sigue aportando ${formatearDineroQuri(necesarioMensual(fondo))} al mes. $consejo", "You are on track: keep adding ${formatearDineroQuri(necesarioMensual(fondo))} per month. $consejo")
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { editando = true }) {
                        Text(quriTexto("Editar", "Edit"))
                    }
                    TextButton(onClick = { mostrarConfirmacionBorrado = true }) {
                        Text(quriTexto("Eliminar", "Delete"), color = RojoPeligro)
                    }
                }
            }
        }
    }
}

private fun categoriaInnecesariaPrincipal(gastos: List<GastoEntidad>): String? =
    gastos
        .filter { it.etiqueta == "Innecesario" }
        .groupBy { it.categoria }
        .mapValues { (_, lista) -> lista.sumOf { it.cantidadCentimos } }
        .maxByOrNull { it.value }
        ?.key

private fun diasRestantes(fechaLimite: String): Long? =
    try {
        ChronoUnit.DAYS.between(java.time.LocalDate.now(), FechaQuri.parsear(fechaLimite)).coerceAtLeast(0L)
    } catch (_: Exception) {
        null
    }

private fun progresoTiempo(fechaLimite: String): Float {
    val limite = try {
        FechaQuri.parsear(fechaLimite)
    } catch (_: Exception) {
        return 0f
    }
    val hoy = java.time.LocalDate.now()
    val referencia = limite.minusYears(1)
    val totalDias = ChronoUnit.DAYS.between(referencia, limite).coerceAtLeast(1L)
    val diasConsumidos = ChronoUnit.DAYS.between(referencia, hoy).coerceIn(0L, totalDias)
    return (diasConsumidos.toDouble() / totalDias.toDouble()).toFloat()
}

@Composable
private fun textoPrioridad(prioridad: Int): String =
    when (prioridad) {
        1 -> quriTexto("Alta", "High")
        3 -> quriTexto("Baja", "Low")
        else -> quriTexto("Media", "Medium")
    }

@Composable
private fun estadoFondo(fondo: MetaEntidad): String {
    val porcentaje = if (fondo.cantidadObjetivoCentimos > 0L) {
        (fondo.cantidadActualCentimos.toFloat() / fondo.cantidadObjetivoCentimos.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    return if (porcentaje >= progresoTiempo(fondo.fechaLimite)) quriTexto("Vas bien", "On track") else quriTexto("Vas tarde", "Behind")
}

private fun necesarioMensual(fondo: MetaEntidad): Long {
    val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
    val meses = try {
        ChronoUnit.MONTHS.between(
            java.time.LocalDate.now().withDayOfMonth(1),
            FechaQuri.parsear(fondo.fechaLimite).withDayOfMonth(1)
        ).coerceAtLeast(1L)
    } catch (_: Exception) {
        1L
    }
    return (restante + meses - 1L) / meses
}
