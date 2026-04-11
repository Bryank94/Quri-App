package com.example.quritfg.ui.pantallas.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

import com.example.quritfg.ui.viewmodels.RegistroViewModel
import com.example.quritfg.ui.navegacion.Rutas

/**
 * Pantalla de registro.
 *
 * Permite crear una cuenta basica.
 * La validacion se hace en el ViewModel.
 */
@Composable
fun RegistroPantalla(navController: NavController) {

    // viewmodel (controla estados y errores)
    val vm: RegistroViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top // importante para que no se centre
    ) {

        Spacer(modifier = Modifier.height(24.dp)) // separacion arriba

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // input correo
        OutlinedTextField(
            value = vm.correo,
            onValueChange = { vm.onCorreoCambiado(it) },
            label = { Text("Correo electrónico") },
            isError = vm.errorCorreo != null,
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        // mensaje de error correo
        if (vm.errorCorreo != null) {
            Text(
                text = vm.errorCorreo!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // input contraseña
        OutlinedTextField(
            value = vm.contrasena,
            onValueChange = { vm.onContrasenaCambiada(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = vm.errorContrasena != null,
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        // mensaje de error contraseña
        if (vm.errorContrasena != null) {
            Text(
                text = vm.errorContrasena!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        /**
         * Boton registro
         *
         * solo funciona si los datos son validos
         */
        Button(
            onClick = {
                if (vm.registroValido) {
                    navController.navigate(Rutas.ConfiguracionMeta.ruta)
                }
            },
            enabled = vm.registroValido,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear cuenta")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ir a login
        TextButton(
            onClick = {
                navController.navigate(Rutas.Login.ruta)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }
}