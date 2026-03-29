package com.example.quritfg.ui.pantallas.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModel
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModelFactory

/**
 * Pantalla donde el usuario define su meta de ahorro.
 * Puede usarse como configuracion inicial o como edicion posterior.
 */
@Composable
fun ConfiguracionMetaPantalla(
    navController: NavController,
    esPrimeraConfiguracion: Boolean = false
) {

    // Se obtiene el repositorio y se inyecta en el ViewModel
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: ConfiguracionMetaViewModel = viewModel(
        factory = ConfiguracionMetaViewModelFactory(repositorio)
    )

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Configura tu meta",
                style = MaterialTheme.typography.headlineMedium
            )

            // Campo para el nombre de la meta
            OutlinedTextField(
                value = vm.nombreMeta,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre de la meta") },
                isError = vm.errorNombre != null,
                supportingText = {
                    vm.errorNombre?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para la cantidad objetivo
            OutlinedTextField(
                value = vm.cantidadObjetivo,
                onValueChange = vm::onCantidadObjetivoChange,
                label = { Text("Cantidad objetivo") },
                isError = vm.errorCantidad != null,
                supportingText = {
                    vm.errorCantidad?.let { Text(it) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Boton que guarda la meta y navega
            Button(
                onClick = {
                    vm.guardarMetaInicial()

                    if (esPrimeraConfiguracion) {
                        navController.navigate(Rutas.Inicio.ruta) {
                            popUpTo(Rutas.Registro.ruta) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Rutas.Inicio.ruta) {
                            launchSingleTop = true
                        }
                    }
                },
                enabled = vm.puedeGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}