package com.example.quritfg.ui.pantallas.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.navegacion.Rutas
import com.example.quritfg.ui.viewmodels.AutenticacionViewModelFactory
import com.example.quritfg.ui.viewmodels.AutentificacionViewModel

@Composable
fun LoginPantalla(navController: NavController) {

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val sesionManager = SesionManager(context)
    val autenticacionVm: AutentificacionViewModel = viewModel(
        factory = AutenticacionViewModelFactory(repositorio)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Iniciar sesion",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                errorLogin = null
            },
            label = { Text("Correo electronico") },
            isError = errorLogin != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                errorLogin = null
            },
            label = { Text("Contrasena") },
            visualTransformation = PasswordVisualTransformation(),
            isError = errorLogin != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        if (errorLogin != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorLogin!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                autenticacionVm.iniciarSesion(correo, contrasena) { usuario ->
                    if (usuario != null) {
                        sesionManager.guardarSesionActiva(usuario.id, usuario.email)

                        navController.navigate(Rutas.Inicio.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    } else {
                        errorLogin = "Correo o contrasena incorrectos"
                    }
                }
            },
            enabled = correo.isNotBlank() && contrasena.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                navController.navigate(Rutas.Registro.ruta)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("No tienes cuenta? Crear cuenta")
        }
    }
}
