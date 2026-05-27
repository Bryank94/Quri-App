package com.example.quritfg.ui.pantallas.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.analytics.LocalAnalyticsTracker
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.config.quriValor
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModel
import com.example.quritfg.ui.viewmodels.ConfiguracionMetaViewModelFactory

/**
 * Pantalla para configurar la meta de ahorro.
 *
 * Se usa tanto al principio (primera vez)
 * como despues desde la app.
 */
@Composable
fun ConfiguracionMetaPantalla(
    navController: NavController,
    esPrimeraConfiguracion: Boolean = false
) {

    // contexto + repositorio
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)

    // viewmodel (gestiona estado y validaciones)
    val analytics = LocalAnalyticsTracker(context)
    val vm: ConfiguracionMetaViewModel = viewModel(
        factory = ConfiguracionMetaViewModelFactory(repositorio, analytics)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = quriTexto("Configura tu fondo", "Set up your fund"),
            style = MaterialTheme.typography.headlineSmall
        )

        // input nombre de la meta
        OutlinedTextField(
            value = vm.nombreFondo,
            onValueChange = vm::onNombreChange,
            label = { Text(quriTexto("Nombre del fondo", "Fund name")) },
            isError = vm.errorNombre != null,
            supportingText = {
                vm.errorNombre?.let { Text(quriValor(it)) }
            },
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        // input cantidad objetivo
        OutlinedTextField(
            value = vm.cantidadObjetivo,
            onValueChange = vm::onCantidadObjetivoChange,
            label = { Text(quriTexto("Cantidad objetivo", "Target amount")) },
            isError = vm.errorCantidad != null,
            supportingText = {
                vm.errorCantidad?.let { Text(quriValor(it)) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        OutlinedTextField(
            value = vm.fechaLimite,
            onValueChange = vm::onFechaLimiteChange,
            label = { Text(quriTexto("Fecha limite (dd-MM-yyyy)", "Deadline (dd-MM-yyyy)")) },
            isError = vm.errorFechaLimite != null,
            supportingText = {
                vm.errorFechaLimite?.let { Text(quriValor(it)) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        OutlinedTextField(
            value = vm.ahorroActual,
            onValueChange = vm::onAhorroActualChange,
            label = { Text(quriTexto("Ahorro actual", "Current saving")) },
            isError = vm.errorAhorroActual != null,
            supportingText = {
                vm.errorAhorroActual?.let { Text(quriValor(it)) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Text(
            text = quriTexto("Prioridad", "Priority"),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                1 to quriTexto("Alta", "High"),
                2 to quriTexto("Media", "Medium"),
                3 to quriTexto("Baja", "Low")
            ).forEach { (valor, etiqueta) ->
                FilterChip(
                    selected = vm.prioridad == valor,
                    onClick = { vm.onPrioridadChange(valor) },
                    label = { Text(etiqueta) }
                )
            }
        }

        /**
         * Boton guardar fondo
         *
         * cambia comportamiento si es primera vez
         */
        Button(
            onClick = {
                vm.guardarMetaInicial() // guarda en bd

                if (esPrimeraConfiguracion) {
                    // elimina pantallas anteriores (registro)
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

            shape = RoundedCornerShape(12.dp)
        ) {
            Text(quriTexto("Guardar", "Save"))
        }
    }
}
