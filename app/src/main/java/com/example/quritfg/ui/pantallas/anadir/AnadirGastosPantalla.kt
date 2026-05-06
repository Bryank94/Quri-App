package com.example.quritfg.ui.pantallas.anadir

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.quritfg.ui.viewmodels.ConceptoIngreso
import com.example.quritfg.ui.viewmodels.EtiquetaGasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirGastoPantalla(navController: NavController) {

    // contexto + repositorio
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    // viewmodel (clave porque gestiona toda la logica)
    val vm: AnadirMovimientoViewModel = viewModel(
        factory = AnadirMovimientoViewModelFactory(repositorio)
    )

    // estado del dropdown
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // titulo cambia segun tipo (gasto o ingreso)
        Text(
            text = if (vm.tipoMovimiento == TipoMovimiento.GASTO)
                "Añadir gasto"
            else
                "Añadir ingreso",
            style = MaterialTheme.typography.headlineSmall
        )

        // selector gasto / ingreso
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

        // fecha actual (viene del viewmodel)
        Text(
            text = "Fecha: ${vm.fecha}",
            style = MaterialTheme.typography.bodyMedium
        )

        /**
         * Dropdown para categoria
         * solo se usa en gastos
         */
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
                    .fillMaxWidth(),

                // estilo visual
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {

                // muestra categorias si es gasto y conceptos si es ingreso
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
                } else {
                    ConceptoIngreso.entries.forEach { concepto ->

                        DropdownMenuItem(
                            text = { Text(concepto.texto) },
                            onClick = {
                                vm.onCategoriaChange(concepto.texto)
                                expandido = false
                            }
                        )
                    }
                }
            }
        }

        // input de cantidad
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
            modifier = Modifier.fillMaxWidth(),

            // estilo
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        if (vm.tipoMovimiento == TipoMovimiento.GASTO) {
            Text(
                text = "Etiqueta del gasto",
                style = MaterialTheme.typography.titleSmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EtiquetaGasto.entries.forEach { etiqueta ->
                    FilterChip(
                        selected = vm.etiquetaGasto == etiqueta.texto,
                        onClick = { vm.onEtiquetaGastoChange(etiqueta) },
                        label = { Text(etiqueta.texto) }
                    )
                }
            }
        }

        /**
         * Boton guardar
         *
         * Solo se activa si los datos son validos
         */
        Button(
            onClick = {
                vm.guardarMovimiento() // guarda en bd
                navController.navigate(Rutas.Inicio.ruta) {
                    launchSingleTop = true
                }
            },
            enabled = vm.puedeGuardar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Guardar")
        }
    }
}
