package com.example.quritfg.ui.pantallas.anadir

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.AnadirMovimientoViewModel
import com.example.quritfg.ui.viewmodels.AnadirMovimientoViewModelFactory
import com.example.quritfg.ui.viewmodels.TipoMovimiento
import com.example.quritfg.ui.viewmodels.CategoriaGasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirGastoPantalla(navController: NavController) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: AnadirMovimientoViewModel = viewModel(
        factory = AnadirMovimientoViewModelFactory(repositorio)
    )

    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = if (vm.tipoMovimiento == TipoMovimiento.GASTO)
                "Añadir gasto"
            else
                "Añadir ingreso",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            FilterChip(
                selected = vm.tipoMovimiento == TipoMovimiento.GASTO,
                onClick = { vm.onTipoMovimientoChange(TipoMovimiento.GASTO) },
                label = { Text("Gasto") }
            )

            FilterChip(
                selected = vm.tipoMovimiento == TipoMovimiento.INGRESO,
                onClick = { vm.onTipoMovimientoChange(TipoMovimiento.INGRESO) },
                label = { Text("Ingreso") }
            )
        }

        Text(
            text = "Fecha: ${vm.fecha}",
            style = MaterialTheme.typography.bodyMedium
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
                        if (vm.tipoMovimiento == TipoMovimiento.GASTO)
                            "Categoría"
                        else
                            "Concepto"
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {

                if (vm.tipoMovimiento == TipoMovimiento.GASTO) {
                    CategoriaGasto.entries.forEach { categoria ->

                        DropdownMenuItem(
                            text = { Text(categoria.texto) },
                            onClick = {
                                vm.onCategoriaChange(categoria.texto)
                                expandido = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = vm.cantidad,
            onValueChange = vm::onCantidadChange,
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = vm.errorCantidad != null,
            supportingText = {
                vm.errorCantidad?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                vm.guardarMovimiento()
                navController.navigate(Rutas.Inicio.ruta) {
                    launchSingleTop = true
                }
            },
            enabled = vm.puedeGuardar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}