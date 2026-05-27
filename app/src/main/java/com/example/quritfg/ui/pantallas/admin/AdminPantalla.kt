package com.example.quritfg.ui.pantallas.admin

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quritfg.datos.di.ModuloApp
import com.example.quritfg.datos.local.UsuarioEntidad
import com.example.quritfg.ui.theme.DoradoDinero

private const val CODIGO_ADMIN_DEBUG = "AUDITORIA-LOCAL"

@Composable
fun AdminPantalla(navController: NavController) {
    val context = LocalContext.current
    val esBuildDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val repositorio = remember { ModuloApp.proporcionarRepositorio(context) }
    var desbloqueado by remember { mutableStateOf(false) }
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Administrador",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        if (!esBuildDebug) {
            TarjetaAdmin {
                Text(
                    text = "Acceso no disponible",
                    color = DoradoDinero,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "El panel interno solo esta habilitado en compilaciones locales de depuracion.",
                    color = Color.White.copy(alpha = 0.82f)
                )
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
            return@Column
        }

        if (!desbloqueado) {
            TarjetaAdmin {
                Text(
                    text = "Acceso local de auditoria",
                    color = DoradoDinero,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Disponible solo en debug para revisar datos locales durante pruebas.",
                    color = Color.White.copy(alpha = 0.82f)
                )
                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        error = false
                    },
                    label = { Text("Codigo de administrador") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error) {
                    Text(
                        text = "Codigo incorrecto",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Volver")
                    }
                    Button(
                        onClick = {
                            desbloqueado = codigo.trim() == CODIGO_ADMIN_DEBUG
                            error = !desbloqueado
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Entrar")
                    }
                }
            }
            return@Column
        }

        val usuarios by repositorio.obtenerUsuariosAdmin().collectAsState(initial = emptyList())
        val gastos by repositorio.contarGastosAdmin().collectAsState(initial = 0)
        val ingresos by repositorio.contarIngresosAdmin().collectAsState(initial = 0)
        val fondos by repositorio.contarFondosAdmin().collectAsState(initial = 0)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResumenAdmin("Cuentas", usuarios.size.toString(), Modifier.weight(1f))
            ResumenAdmin("Fondos", fondos.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResumenAdmin("Gastos", gastos.toString(), Modifier.weight(1f))
            ResumenAdmin("Ingresos", ingresos.toString(), Modifier.weight(1f))
        }

        TarjetaAdmin {
            Text(
                text = "Cuentas creadas",
                color = DoradoDinero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (usuarios.isEmpty()) {
                Text("Todavia no hay cuentas registradas.", color = Color.White.copy(alpha = 0.82f))
            } else {
                usuarios.forEach { usuario ->
                    UsuarioAdminFila(usuario)
                }
            }
        }

        TarjetaAdmin {
            Text(
                text = "Informacion util",
                color = DoradoDinero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("La app ya cuenta usuarios, fondos, gastos e ingresos guardados en local.", color = Color.White.copy(alpha = 0.82f))
            Text("El origen mostrado se deduce por el correo porque la version actual no guardaba el metodo exacto de alta.", color = Color.White.copy(alpha = 0.72f))
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun ResumenAdmin(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, DoradoDinero.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .background(Color(0x9905160C), RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = DoradoDinero, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(titulo, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun TarjetaAdmin(contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DoradoDinero.copy(alpha = 0.62f), RoundedCornerShape(14.dp))
            .background(Color(0x9905160C), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = contenido
    )
}

@Composable
private fun UsuarioAdminFila(usuario: UsuarioEntidad) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DoradoDinero.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(usuario.email, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(3.dp))
        Text("ID local: ${usuario.id}", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
        Text("Origen probable: ${origenProbable(usuario.email)}", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
    }
}

private fun origenProbable(email: String): String =
    when {
        email.endsWith("@gmail.com") -> "Google/Gmail"
        email.endsWith("@outlook.com") || email.endsWith("@hotmail.com") -> "Correo Microsoft"
        else -> "Correo y contrasena"
    }
