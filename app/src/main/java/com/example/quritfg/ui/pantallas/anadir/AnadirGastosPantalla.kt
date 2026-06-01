package com.example.quritfg.ui.pantallas.anadir

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.config.formatearDineroQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.config.quriValor
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.AnadirMovimientoViewModel
import com.example.quritfg.ui.viewmodels.AnadirMovimientoViewModelFactory
import com.example.quritfg.ui.viewmodels.CategoriaGasto
import com.example.quritfg.ui.viewmodels.ConceptoIngreso
import com.example.quritfg.ui.viewmodels.EtiquetaGasto
import com.example.quritfg.ui.viewmodels.TipoMovimiento
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.RojoPeligro
import com.example.quritfg.ui.theme.VerdeDinero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirGastoPantalla(navController: NavController) {
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val vm: AnadirMovimientoViewModel = viewModel(
        factory = AnadirMovimientoViewModelFactory(repositorio)
    )

    LaunchedEffect(vm.guardadoCorrectamente) {
        if (vm.guardadoCorrectamente) {
            vm.consumirGuardadoCorrecto()
            navController.navigate(Rutas.Inicio.ruta) {
                launchSingleTop = true
            }
        }
    }

    var expandido by remember { mutableStateOf(false) }
    val fondos by vm.fondos.collectAsState(initial = emptyList())
    val fondosDisponibles = fondos.filter { fondo ->
        !vm.aportacionesFondos.containsKey(fondo.id) || fondo.id == vm.fondoDistribucionSeleccionadoId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (vm.tipoMovimiento == TipoMovimiento.GASTO) quriTexto("Anadir gasto", "Add expense") else quriTexto("Anadir ingreso", "Add income"),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterChip(
                selected = vm.tipoMovimiento == TipoMovimiento.GASTO,
                onClick = { vm.onTipoMovimientoChange(TipoMovimiento.GASTO) },
                label = { Text(quriTexto("Gasto", "Expense")) },
                colors = chipDorado(vm.tipoMovimiento == TipoMovimiento.GASTO),
                border = BorderStroke(1.dp, DoradoDinero)
            )

            FilterChip(
                selected = vm.tipoMovimiento == TipoMovimiento.INGRESO,
                onClick = { vm.onTipoMovimientoChange(TipoMovimiento.INGRESO) },
                label = { Text(quriTexto("Ingreso", "Income")) },
                colors = chipDorado(vm.tipoMovimiento == TipoMovimiento.INGRESO),
                border = BorderStroke(1.dp, DoradoDinero)
            )
        }

        Text(
            text = "${quriTexto("Fecha", "Date")}: ${vm.fecha}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = vm.categoria,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        if (vm.tipoMovimiento == TipoMovimiento.GASTO) quriTexto("Categoria", "Category") else quriTexto("Concepto", "Concept")
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = coloresCampoOscuro()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                containerColor = Color(0xCC04160D),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                if (vm.tipoMovimiento == TipoMovimiento.GASTO) {
                    CategoriaGasto.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(quriValor(categoria.texto), color = Color.White) },
                            onClick = {
                                vm.onCategoriaChange(categoria.texto)
                                expandido = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.White,
                                leadingIconColor = DoradoDinero,
                                trailingIconColor = DoradoDinero
                            )
                        )
                    }
                } else {
                    ConceptoIngreso.entries.forEach { concepto ->
                        DropdownMenuItem(
                            text = { Text(quriValor(concepto.texto), color = Color.White) },
                            onClick = {
                                vm.onCategoriaChange(concepto.texto)
                                expandido = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.White,
                                leadingIconColor = DoradoDinero,
                                trailingIconColor = DoradoDinero
                            )
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = vm.cantidad,
            onValueChange = vm::onCantidadChange,
            label = { Text(quriTexto("Cantidad total", "Total amount")) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = vm.errorCantidad != null,
            supportingText = {
                vm.errorCantidad?.let { Text(quriValor(it)) }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = coloresCampoOscuro()
        )

        if (vm.tipoMovimiento == TipoMovimiento.INGRESO && fondos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = quriTexto("Distribucion a fondos", "Fund distribution"),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )

                Button(
                    onClick = { vm.distribuirAutomaticamente(fondos) },
                    enabled = fondos.isNotEmpty() && textoACentimos(vm.cantidad) != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DoradoDinero,
                        contentColor = Color(0xFF06160D),
                        disabledContainerColor = DoradoDinero.copy(alpha = 0.30f),
                        disabledContentColor = Color.White.copy(alpha = 0.55f)
                    )
                ) {
                    Text(quriTexto("Distribuir automaticamente cada mes", "Distribute automatically every month"))
                }
                Text(
                    text = quriTexto("Elige el fondo a reforzar", "Choose the fund to reinforce"),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.90f)
                )

                if (fondosDisponibles.isEmpty()) {
                    Text(
                        text = quriTexto(
                            "Todos los fondos ya tienen una cantidad asignada. Quita uno si quieres cambiarlo.",
                            "Every fund already has an assigned amount. Remove one if you want to change it."
                        ),
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        fondosDisponibles.forEach { fondo ->
                            val seleccionado = fondo.id == vm.fondoDistribucionSeleccionadoId
                            FilterChip(
                                selected = seleccionado,
                                onClick = { vm.onFondoDistribucionChange(fondo.id) },
                                label = {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(fondo.nombre)
                                        Text(
                                            text = "${formatearDineroQuri(fondo.cantidadActualCentimos)} / ${formatearDineroQuri(fondo.cantidadObjetivoCentimos)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = chipDorado(seleccionado),
                                border = BorderStroke(1.dp, if (seleccionado) DoradoDinero else DoradoDinero.copy(alpha = 0.65f))
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = vm.cantidadDistribucion,
                        onValueChange = vm::onCantidadDistribucionChange,
                        label = { Text(quriTexto("Cantidad para este fondo", "Amount for this fund")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampoOscuro()
                    )

                    Button(
                        onClick = { vm.anadirAportacionSeleccionada() },
                        enabled = vm.puedeAnadirAportacion,
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DoradoDinero,
                            contentColor = Color(0xFF06160D),
                            disabledContainerColor = DoradoDinero.copy(alpha = 0.30f),
                            disabledContentColor = Color.White.copy(alpha = 0.55f)
                        )
                    ) {
                        Text(quriTexto("Anadir", "Add"))
                    }
                }

                vm.aportacionesFondos.forEach { (fondoId, cantidadTexto) ->
                    val fondo = fondos.firstOrNull { it.id == fondoId } ?: return@forEach

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${fondo.nombre}: ${formatearDineroQuri(textoACentimos(cantidadTexto) ?: 0L)}",
                            color = Color.White
                        )
                        TextButton(onClick = { vm.quitarAportacion(fondoId) }) {
                            Text(quriTexto("Quitar", "Remove"), color = RojoPeligro)
                        }
                    }
                }

                Text("${quriTexto("Asignado a fondos", "Assigned to funds")}: ${formatearDineroQuri(vm.totalAportacionesCentimos())}", color = DoradoDinero)
                Text("${quriTexto("Sin asignar", "Unassigned")}: ${formatearDineroQuri(vm.remanenteCentimos())}", color = Color.White)

                vm.errorAportaciones?.let { mensaje ->
                    Text(
                        text = quriValor(mensaje),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (vm.tipoMovimiento == TipoMovimiento.GASTO) {
            Text(
                text = quriTexto("Etiqueta del gasto", "Expense tag"),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EtiquetaGasto.entries.forEach { etiqueta ->
                    FilterChip(
                        selected = vm.etiquetaGasto == etiqueta.texto,
                        onClick = { vm.onEtiquetaGastoChange(etiqueta) },
                        label = { Text(quriValor(etiqueta.texto)) },
                        colors = chipEtiqueta(etiqueta, vm.etiquetaGasto == etiqueta.texto),
                        border = BorderStroke(
                            1.dp,
                            when (etiqueta) {
                                EtiquetaGasto.INNECESARIO -> RojoPeligro
                                EtiquetaGasto.NECESARIO -> VerdeDinero
                                EtiquetaGasto.IMPORTANTE -> DoradoDinero
                            }
                        )
                    )
                }
            }
        }

        Button(
            onClick = { vm.guardarMovimiento() },
            enabled = vm.puedeGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DoradoDinero,
                contentColor = Color(0xFF06160D),
                disabledContainerColor = DoradoDinero.copy(alpha = 0.30f),
                disabledContentColor = Color.White.copy(alpha = 0.50f)
            )
        ) {
            Text(if (vm.guardando) quriTexto("Guardando...", "Saving...") else quriTexto("Guardar", "Save"))
        }

        vm.errorGuardado?.let { mensaje ->
            Text(
                text = quriValor(mensaje),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun coloresCampoOscuro() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.55f),
    focusedContainerColor = Color(0x8804160D),
    unfocusedContainerColor = Color(0x6604160D),
    focusedIndicatorColor = DoradoDinero,
    unfocusedIndicatorColor = DoradoDinero,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.86f),
    cursorColor = DoradoDinero,
    errorIndicatorColor = RojoPeligro,
    errorLabelColor = RojoPeligro,
    errorTextColor = Color.White
)

@Composable
private fun chipDorado(seleccionado: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = DoradoDinero,
    selectedLabelColor = Color(0xFF06160D),
    containerColor = Color(0x6604160D),
    labelColor = Color.White
)

@Composable
private fun chipEtiqueta(
    etiqueta: EtiquetaGasto,
    seleccionado: Boolean
) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = when (etiqueta) {
        EtiquetaGasto.INNECESARIO -> RojoPeligro
        EtiquetaGasto.NECESARIO -> VerdeDinero
        EtiquetaGasto.IMPORTANTE -> DoradoDinero
    },
    selectedLabelColor = if (etiqueta == EtiquetaGasto.IMPORTANTE) Color(0xFF06160D) else Color.White,
    containerColor = Color(0x6604160D),
    labelColor = Color.White
)

