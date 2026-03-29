package com.example.quritfg.ui.pantallas.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quritfg.ui.viewmodels.RegistroViewModel
import com.example.quritfg.ui.navegacion.Rutas

/**
 * Pantalla de registro inicial.
 * Valida correo y contraseña antes de permitir continuar.
 */
@Composable
fun RegistroPantalla(navController: NavController) {

    // Se crea el ViewModel que controla validaciones
    val vm: RegistroViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para introducir el correo
        OutlinedTextField(
            value = vm.correo,
            onValueChange = { vm.onCorreoCambiado(it) },
            label = { Text("Correo electrónico") },
            isError = vm.errorCorreo != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Muestra mensaje si hay error en el correo
        if (vm.errorCorreo != null) {
            Text(
                text = vm.errorCorreo!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para la contraseña
        OutlinedTextField(
            value = vm.contrasena,
            onValueChange = { vm.onContrasenaCambiada(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = vm.errorContrasena != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Muestra mensaje si hay error en la contraseña
        if (vm.errorContrasena != null) {
            Text(
                text = vm.errorContrasena!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Solo permite avanzar si la validacion es correcta
        Button(
            onClick = {
                if (vm.registroValido) {
                    navController.navigate(Rutas.ConfiguracionMeta.ruta)
                }
            },
            enabled = vm.registroValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}