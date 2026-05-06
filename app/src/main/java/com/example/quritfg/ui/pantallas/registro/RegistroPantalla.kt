package com.example.quritfg.ui.pantallas.registro

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.ui.viewmodels.AutenticacionViewModelFactory
import com.example.quritfg.ui.viewmodels.AutentificacionViewModel
import com.example.quritfg.ui.viewmodels.RegistroViewModel
import com.example.quritfg.ui.navegacion.Rutas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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
    val context = LocalContext.current
    val repositorio = ModuloApp.proporcionarRepositorio(context)
    val sesionManager = SesionManager(context)
    val autenticacionVm: AutentificacionViewModel = viewModel(
        factory = AutenticacionViewModelFactory(repositorio)
    )
    var mensajeSocial by remember { mutableStateOf<String?>(null) }
    val webClientId = remember(context) {
        val recurso = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )

        if (recurso == 0) null else context.getString(recurso)
    }
    val googleSignInClient = remember(context) {
        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId ?: "")
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, opciones)
    }
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val tareaCuenta = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)

        try {
            val cuenta = tareaCuenta.getResult(ApiException::class.java)
            val idToken = cuenta.idToken

            if (idToken == null) {
                mensajeSocial = "No se pudo obtener el token de Google."
                return@rememberLauncherForActivityResult
            }

            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credencial)
                .addOnCompleteListener { tareaAuth ->
                    if (tareaAuth.isSuccessful) {
                        val email = tareaAuth.result?.user?.email

                        if (email == null) {
                            mensajeSocial = "Google no devolvio un correo valido."
                            return@addOnCompleteListener
                        }

                        autenticacionVm.iniciarSesionExterna(email) { usuario ->
                            if (usuario != null) {
                                sesionManager.guardarSesionActiva(usuario.id, usuario.email)
                                navController.navigate(Rutas.ConfiguracionMeta.ruta)
                            } else {
                                mensajeSocial = "No se pudo crear la sesion local."
                            }
                        }
                    } else {
                        mensajeSocial = "No se pudo iniciar sesion con Gmail."
                    }
                }
        } catch (_: ApiException) {
            mensajeSocial = "Inicio de sesion con Gmail cancelado o no disponible."
        }
    }

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
                    autenticacionVm.registrarUsuario(vm.correo, vm.contrasena) { usuario ->
                        if (usuario != null) {
                            sesionManager.guardarSesionActiva(usuario.id, usuario.email)
                            navController.navigate(Rutas.ConfiguracionMeta.ruta)
                        }
                    }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (webClientId == null) {
                        mensajeSocial = "Falta configurar SHA-1 en Firebase y descargar de nuevo google-services.json."
                    } else {
                        googleLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gmail")
            }

            OutlinedButton(
                onClick = {
                    mensajeSocial = "Para crear cuenta con Facebook falta conectar Firebase Auth."
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Facebook")
            }
        }

        mensajeSocial?.let { mensaje ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
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
