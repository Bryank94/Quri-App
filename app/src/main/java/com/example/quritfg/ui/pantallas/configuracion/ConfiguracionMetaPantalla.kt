package com.example.quritfg.ui.pantallas.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModel
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModelFactory

@Composable
fun ConfiguracionMetaPantalla(
    navController: NavController,
    esPrimeraConfiguracion: Boolean = false
) {

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    val vm: ConfiguracionMetaViewModel = viewModel(
        factory = ConfiguracionMetaViewModelFactory(repositorio)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Configura tu meta",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = vm.nombreMeta,
            onValueChange = vm::onNombreChange,
            label = { Text("Nombre de la meta") },
            isError = vm.errorNombre != null,
            supportingText = {
                vm.errorNombre?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth(),

            // 🔥 NUEVO
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

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
            modifier = Modifier.fillMaxWidth(),

            // 🔥 NUEVO
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

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
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            // 🔥 NUEVO
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Guardar")
        }
    }
}