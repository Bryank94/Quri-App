package com.example.quritfg.ui.componentes

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quritfg.R
import com.example.quritfg.datos.SesionManager
import com.example.quritfg.datos.modelo.RecompensasQuri
import com.example.quritfg.ui.config.LocalQuriSettings
import com.example.quritfg.ui.theme.DoradoDinero

@Composable
fun BarraSuperior(
    onLogoClick: (() -> Unit)? = null,
    mostrarMenuUsuario: Boolean = true,
    puntosQuri: Int? = null,
    onAdminClick: (() -> Unit)? = null
) {
    var panelAbierto by remember { mutableStateOf<PanelUsuario?>(null) }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (mostrarMenuUsuario) {
                if (puntosQuri != null) {
                    BarraProgresoPuntosSuperior(
                        puntos = puntosQuri,
                        onClick = { panelAbierto = PanelUsuario.Puntos },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 20.dp, end = 78.dp)
                    )
                }
                MenuUsuario(
                    onAbrirPanel = { panelAbierto = it },
                    onCerrarSesion = onLogoClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 18.dp)
                )
            } else if (onAdminClick != null) {
                TextButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp, top = 34.dp)
                ) {
                    Text(
                        text = "administrador",
                        color = DoradoDinero,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    PanelUsuarioDialogo(
        panel = panelAbierto,
        onAbrirPanel = { panelAbierto = it },
        onVolver = { panelAbierto = PanelUsuario.Menu },
        onCerrar = { panelAbierto = null }
    )
}

@Composable
private fun BarraProgresoPuntosSuperior(
    puntos: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progreso = (puntos.toFloat() / RecompensasQuri.PUNTOS_MINIMOS_CANJE.toFloat()).coerceIn(0f, 1f)
    val colorBarra = when {
        progreso < 0.34f -> Color(0xFFD94A4A)
        progreso < 0.78f -> Color(0xFF2F80ED)
        else -> Color(0xFF25B86A)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 250.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x5506140A))
            .border(1.dp, colorBarra.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progreso)
                .height(34.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(colorBarra)
        )
        Text(
            text = "$puntos pts",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 14.dp)
        )
    }
}

private enum class PanelUsuario {
    Menu,
    Perfil,
    Configuracion,
    Estadisticas,
    Actividad,
    Ayuda,
    Consejo,
    Puntos
}

@Composable
private fun MenuUsuario(
    onAbrirPanel: (PanelUsuario) -> Unit,
    onCerrarSesion: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    val settings = LocalQuriSettings.current

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.quri_profile),
            contentDescription = "Menu de usuario",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xDD062416))
                .border(1.dp, DoradoDinero.copy(alpha = 0.9f), CircleShape)
                .clickable { expandido = true }
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            containerColor = Color(0xE805160C),
            modifier = Modifier
                .width(260.dp)
                .background(Color(0xE805160C), RoundedCornerShape(18.dp))
        ) {
            ItemMenuUsuario(settings.texto("Perfil", "Profile")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Perfil)
            }
            ItemMenuUsuario(settings.texto("Configuracion", "Settings")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Configuracion)
            }
            ItemMenuUsuario(settings.texto("Estadisticas personales", "Personal stats")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Estadisticas)
            }
            ItemMenuUsuario(settings.texto("Centro de actividad", "Activity center")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Actividad)
            }
            ItemMenuUsuario(settings.texto("Ayuda y soporte", "Help and support")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Ayuda)
            }
            ItemMenuUsuario(settings.texto("Consejo financiero", "Financial tip")) {
                expandido = false
                onAbrirPanel(PanelUsuario.Consejo)
            }

            SeparadorMenu()

            DropdownMenuItem(
                text = {
                    Text(
                        text = settings.texto("Cerrar sesion", "Log out"),
                        color = Color(0xFFFFF4D6),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                onClick = {
                    expandido = false
                    onCerrarSesion?.invoke()
                }
            )
        }
    }
}

@Composable
private fun CabeceraUsuario() {
    val settings = LocalQuriSettings.current
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = settings.texto("Tu espacio Quri", "Your Quri space"),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = settings.texto("Ajustes, seguridad y progreso personal", "Settings, security and personal progress"),
            color = DoradoDinero,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ItemMenuUsuario(
    titulo: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = titulo,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        onClick = onClick
    )
}

@Composable
private fun PanelUsuarioDialogo(
    panel: PanelUsuario?,
    onAbrirPanel: (PanelUsuario) -> Unit,
    onVolver: () -> Unit,
    onCerrar: () -> Unit
) {
    when (panel) {
        PanelUsuario.Menu -> DialogoOpcionesUsuario(onAbrirPanel, onCerrar)
        PanelUsuario.Perfil -> DialogoPerfil(onCerrar, onVolver)
        PanelUsuario.Configuracion -> DialogoConfiguracion(onCerrar, onVolver)
        PanelUsuario.Estadisticas -> DialogoEstadisticas(onCerrar, onVolver)
        PanelUsuario.Actividad -> DialogoActividad(onCerrar, onVolver)
        PanelUsuario.Ayuda -> DialogoAyuda(onCerrar, onVolver)
        PanelUsuario.Consejo -> DialogoConsejo(onCerrar, onVolver)
        PanelUsuario.Puntos -> DialogoPuntosQuri(onCerrar, onVolver)
        null -> Unit
    }
}

@Composable
private fun DialogoOpcionesUsuario(
    onAbrirPanel: (PanelUsuario) -> Unit,
    onCerrar: () -> Unit
) {
    val settings = LocalQuriSettings.current

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(
                text = settings.texto("Menu de usuario", "User menu"),
                color = DoradoDinero,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BotonOpcionPanel(settings.texto("Perfil", "Profile")) { onAbrirPanel(PanelUsuario.Perfil) }
                BotonOpcionPanel(settings.texto("Configuracion", "Settings")) { onAbrirPanel(PanelUsuario.Configuracion) }
                BotonOpcionPanel(settings.texto("Estadisticas personales", "Personal stats")) { onAbrirPanel(PanelUsuario.Estadisticas) }
                BotonOpcionPanel(settings.texto("Centro de actividad", "Activity center")) { onAbrirPanel(PanelUsuario.Actividad) }
                BotonOpcionPanel(settings.texto("Ayuda y soporte", "Help and support")) { onAbrirPanel(PanelUsuario.Ayuda) }
                BotonOpcionPanel(settings.texto("Consejo financiero", "Financial tip")) { onAbrirPanel(PanelUsuario.Consejo) }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text(settings.texto("Cerrar", "Close")) }
        },
        containerColor = Color(0xEE06160D),
        textContentColor = Color.White
    )
}

@Composable
private fun BotonOpcionPanel(
    texto: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DialogoPerfil(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { preferenciasUsuario(context) }
    val sesionManager = remember { SesionManager(context) }
    val settings = LocalQuriSettings.current

    var nombre by remember { mutableStateOf(prefs.getString("perfil_nombre", "") ?: "") }
    var correo by remember {
        mutableStateOf(prefs.getString("perfil_correo", sesionManager.obtenerEmailUsuario() ?: "") ?: "")
    }
    var metaPrincipal by remember { mutableStateOf(prefs.getString("perfil_meta", "") ?: "") }
    var nivel by remember { mutableStateOf(prefs.getString("perfil_nivel", "Ahorrador inicial") ?: "") }
    var banco by remember { mutableStateOf(prefs.getString("perfil_banco", "") ?: "") }

    DialogoBase(
        titulo = settings.texto("Perfil", "Profile"),
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(settings.texto("Volver", "Back")) }
            Button(
                onClick = {
                    prefs.edit()
                        .putString("perfil_nombre", nombre)
                        .putString("perfil_correo", correo)
                        .putString("perfil_meta", metaPrincipal)
                        .putString("perfil_nivel", nivel)
                        .putString("perfil_banco", banco)
                        .apply()
                    aviso(context, "Perfil guardado")
                    onCerrar()
                }
            ) { Text(settings.texto("Guardar", "Save")) }
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.quri_profile),
            contentDescription = "Icono de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .border(1.dp, DoradoDinero, CircleShape)
        )
        CampoTexto(settings.texto("Nombre", "Name"), nombre) { nombre = it }
        CampoTexto(settings.texto("Correo", "Email"), correo) { correo = it }
        CampoTexto(settings.texto("Meta principal de ahorro", "Main savings goal"), metaPrincipal) { metaPrincipal = it }
        CampoTexto(settings.texto("Nivel o estadistica personal", "Level or personal stat"), nivel) { nivel = it }
        CampoTexto(settings.texto("Cuenta bancaria vinculada", "Linked bank account"), banco) { banco = it }
        Text(
            text = settings.texto("La foto usa el icono de perfil que has enviado.", "The photo uses the profile icon you sent."),
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DialogoConfiguracion(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { preferenciasUsuario(context) }
    val settings = LocalQuriSettings.current

    var idioma by remember(settings.idioma) { mutableStateOf(settings.idioma) }
    var moneda by remember(settings.moneda) { mutableStateOf(settings.moneda) }
    var fecha by remember { mutableStateOf(prefs.getString("config_fecha", "dd-MM-yyyy") ?: "dd-MM-yyyy") }
    var objetivoMensual by remember { mutableStateOf(prefs.getString("config_objetivo_mensual", "") ?: "") }
    var limiteGasto by remember { mutableStateOf(prefs.getString("config_limite_gasto", "") ?: "") }
    var temaOscuro by remember(settings.temaOscuro) { mutableStateOf(settings.temaOscuro) }
    var redondeo by remember { mutableStateOf(prefs.getBoolean("config_redondeo", false)) }
    var ahorroAutomatico by remember { mutableStateOf(prefs.getBoolean("config_ahorro_auto", false)) }
    var recordatorios by remember { mutableStateOf(prefs.getBoolean("config_recordatorios", true)) }
    var alertasMetas by remember { mutableStateOf(prefs.getBoolean("config_alertas_metas", true)) }
    var pin by remember { mutableStateOf(prefs.getBoolean("config_pin", false)) }
    var dobleFactor by remember { mutableStateOf(prefs.getBoolean("config_2fa", false)) }

    DialogoBase(
        titulo = settings.texto("Configuracion", "Settings"),
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(settings.texto("Volver", "Back")) }
            Button(
                onClick = {
                    settings.guardarConfiguracion(
                        idioma = idioma,
                        moneda = moneda,
                        fecha = fecha,
                        objetivoMensual = objetivoMensual,
                        limiteGasto = limiteGasto,
                        temaOscuro = temaOscuro,
                        redondeo = redondeo,
                        ahorroAutomatico = ahorroAutomatico,
                        recordatorios = recordatorios,
                        alertasMetas = alertasMetas,
                        pin = pin,
                        dobleFactor = dobleFactor
                    )
                    aviso(context, settings.texto("Configuracion aplicada", "Settings applied"))
                    onCerrar()
                }
            ) { Text(settings.texto("Guardar", "Save")) }
        }
    ) {
        SelectorSimple(settings.texto("Idioma", "Language"), idioma, listOf("Espanol", "English")) {
            idioma = it
            settings.actualizarIdioma(it)
        }
        SelectorSimple(settings.texto("Moneda", "Currency"), moneda, listOf("EUR", "USD", "MXN")) {
            moneda = it
            settings.actualizarMoneda(it)
        }
        SelectorSimple(settings.texto("Formato de fecha", "Date format"), fecha, listOf("dd-MM-yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd")) { fecha = it }
        CampoTexto(settings.texto("Objetivo mensual automatico", "Automatic monthly goal"), objetivoMensual) { objetivoMensual = it }
        CampoTexto(settings.texto("Limite de gasto mensual", "Monthly spending limit"), limiteGasto) { limiteGasto = it }
        Interruptor(settings.texto("Tema oscuro", "Dark theme"), temaOscuro) {
            temaOscuro = it
            settings.actualizarTemaOscuro(it)
        }
        Interruptor(settings.texto("Redondeo de gastos", "Round-up expenses"), redondeo) { redondeo = it }
        Interruptor(settings.texto("Ahorro automatico", "Automatic saving"), ahorroAutomatico) { ahorroAutomatico = it }
        Interruptor(settings.texto("Recordatorios diarios/semanales", "Daily/weekly reminders"), recordatorios) { recordatorios = it }
        Interruptor(settings.texto("Alertas de metas", "Goal alerts"), alertasMetas) { alertasMetas = it }
        Interruptor(settings.texto("PIN / Face ID / Huella", "PIN / Face ID / Fingerprint"), pin) { pin = it }
        Interruptor(settings.texto("Verificacion en dos pasos", "Two-step verification"), dobleFactor) { dobleFactor = it }
        OutlinedButton(
            onClick = { aviso(context, settings.texto("Datos preparados para exportar", "Data ready to export")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text(settings.texto("Exportar datos", "Export data")) }
        OutlinedButton(
            onClick = { aviso(context, settings.texto("Solicitud de eliminacion registrada", "Deletion request registered")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text(settings.texto("Eliminar cuenta", "Delete account")) }
    }
}

@Composable
private fun DialogoEstadisticas(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { preferenciasUsuario(context) }
    var racha by remember { mutableStateOf(prefs.getString("stats_racha", "12 dias") ?: "12 dias") }
    var mejorMes by remember { mutableStateOf(prefs.getString("stats_mejor_mes", "Mayo") ?: "Mayo") }
    var historico by remember { mutableStateOf(prefs.getString("stats_historico", "0,00") ?: "0,00") }
    var logro by remember { mutableStateOf(prefs.getString("stats_logro", "Constancia") ?: "Constancia") }

    DialogoBase(
        titulo = "Estadisticas personales",
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(LocalQuriSettings.current.texto("Volver", "Back")) }
            Button(
                onClick = {
                    prefs.edit()
                        .putString("stats_racha", racha)
                        .putString("stats_mejor_mes", mejorMes)
                        .putString("stats_historico", historico)
                        .putString("stats_logro", logro)
                        .apply()
                    aviso(context, "Estadisticas actualizadas")
                    onCerrar()
                }
            ) { Text(LocalQuriSettings.current.texto("Guardar", "Save")) }
        }
    ) {
        CampoTexto("Dias ahorrando seguidos", racha) { racha = it }
        CampoTexto("Mejor mes", mejorMes) { mejorMes = it }
        CampoTexto("Total acumulado historico", historico) { historico = it }
        CampoTexto("Logro o insignia principal", logro) { logro = it }
        TarjetaDato("Comparativa mensual", "Usa esta seccion para revisar tu progreso emocional, separada del resumen financiero.")
    }
}

@Composable
private fun DialogoActividad(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { preferenciasUsuario(context) }
    var nota by remember { mutableStateOf(prefs.getString("actividad_nota", "") ?: "") }
    val recomendaciones = listOf(
        "Revisa tus gastos variables antes del domingo.",
        "Tu meta puede mejorar con una aportacion pequena semanal.",
        "Activa recordatorios si quieres mantener la racha."
    )

    DialogoBase(
        titulo = "Centro de actividad",
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(LocalQuriSettings.current.texto("Volver", "Back")) }
            Button(
                onClick = {
                    prefs.edit().putString("actividad_nota", nota).apply()
                    aviso(context, "Actividad guardada")
                    onCerrar()
                }
            ) { Text(LocalQuriSettings.current.texto("Guardar", "Save")) }
        }
    ) {
        TarjetaDato("Notificaciones recientes", "No hay movimientos criticos pendientes.")
        TarjetaDato("Cambios en metas", "Tus metas se actualizaran aqui cuando edites fondos.")
        TarjetaDato("Avisos del sistema", "La app esta funcionando correctamente.")
        recomendaciones.forEach { TarjetaDato("Recomendacion", it) }
        CampoTexto("Nota personal de actividad", nota) { nota = it }
    }
}

@Composable
private fun DialogoAyuda(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var problema by remember { mutableStateOf("") }
    var sugerencia by remember { mutableStateOf("") }

    DialogoBase(
        titulo = "Ayuda y soporte",
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(LocalQuriSettings.current.texto("Volver", "Back")) }
            Button(
                onClick = {
                    aviso(context, "Mensaje enviado al centro de soporte")
                    onCerrar()
                }
            ) { Text(LocalQuriSettings.current.texto("Enviar", "Send")) }
        }
    ) {
        TarjetaDato("FAQ", "Puedes editar fondos, registrar movimientos y revisar tu historial desde la navegacion inferior.")
        TarjetaDato("Tutorial inicial", "Empieza creando un fondo, define una meta y registra ingresos o gastos.")
        CampoTexto("Reportar problema", problema) { problema = it }
        CampoTexto("Sugerencias", sugerencia) { sugerencia = it }
    }
}

@Composable
private fun DialogoConsejo(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { preferenciasUsuario(context) }
    var modoVacaciones by remember { mutableStateOf(prefs.getBoolean("extra_vacaciones", false)) }
    var objetivo by remember { mutableStateOf(prefs.getString("extra_objetivo", "") ?: "") }
    var aportacion by remember { mutableStateOf(prefs.getString("extra_aportacion", "") ?: "") }

    val objetivoNumero = objetivo.replace(",", ".").toFloatOrNull()
    val aportacionNumero = aportacion.replace(",", ".").toFloatOrNull()
    val meses = if (objetivoNumero != null && aportacionNumero != null && aportacionNumero > 0f) {
        kotlin.math.ceil(objetivoNumero / aportacionNumero).toInt().toString()
    } else {
        "-"
    }

    DialogoBase(
        titulo = "Consejo financiero",
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(LocalQuriSettings.current.texto("Volver", "Back")) }
            Button(
                onClick = {
                    prefs.edit()
                        .putBoolean("extra_vacaciones", modoVacaciones)
                        .putString("extra_objetivo", objetivo)
                        .putString("extra_aportacion", aportacion)
                        .apply()
                    aviso(context, "Herramientas guardadas")
                    onCerrar()
                }
            ) { Text(LocalQuriSettings.current.texto("Guardar", "Save")) }
        }
    ) {
        TarjetaDato("Consejo del dia", "Separa el ahorro al recibir ingresos, antes de gastar.")
        TarjetaDato("Reto semanal", "Aparta una cantidad fija durante 7 dias y revisa el resultado.")
        Interruptor("Modo vacaciones", modoVacaciones) { modoVacaciones = it }
        CampoTexto("Simular objetivo", objetivo) { objetivo = it }
        CampoTexto("Aportacion mensual prevista", aportacion) { aportacion = it }
        TarjetaDato("Prediccion de ahorro mensual", "Tiempo estimado para llegar: $meses meses")
    }
}

@Composable
private fun DialogoPuntosQuri(
    onCerrar: () -> Unit,
    onVolver: () -> Unit
) {
    val settings = LocalQuriSettings.current

    DialogoBase(
        titulo = settings.texto("Puntos Quri", "Quri points"),
        onCerrar = onCerrar,
        acciones = {
            TextButton(onClick = onVolver) { Text(settings.texto("Volver", "Back")) }
            Button(onClick = onCerrar) { Text(settings.texto("Entendido", "Got it")) }
        }
    ) {
        TarjetaDato(
            settings.texto("Como funcionan", "How they work"),
            settings.texto(
                "Los puntos Quri premian el buen uso de la app: registrar ingresos, controlar gastos y avanzar en tus fondos.",
                "Quri points reward good app usage: registering income, controlling expenses and progressing in your funds."
            )
        )
        TarjetaDato(
            settings.texto("Como conseguir puntos", "How to earn points"),
            settings.texto(
                "+5 por uso diario, +1 por cada euro neto ahorrado, +100 al completar una meta y +250 por invitar a un amigo activo.",
                "+5 for daily use, +1 for each net euro saved, +100 when completing a goal and +250 for inviting an active friend."
            )
        )
        TarjetaDato(
            settings.texto("Canje", "Redeem"),
            settings.texto(
                "Cuando superas el minimo de puntos, Quri calcula un valor canjeable simbolico. En esta beta sirve para medir progreso y motivacion.",
                "When you pass the minimum points, Quri calculates a symbolic redeemable value. In this beta it measures progress and motivation."
            )
        )
    }
}

@Composable
private fun DialogoBase(
    titulo: String,
    onCerrar: () -> Unit,
    acciones: @Composable () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(
                text = titulo,
                color = DoradoDinero,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = contenido
            )
        },
        confirmButton = acciones,
        containerColor = Color(0xFF06160D),
        titleContentColor = DoradoDinero,
        textContentColor = Color.White
    )
}

@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun Interruptor(
    texto: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}

@Composable
private fun SelectorSimple(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onCambio: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$etiqueta: $valor")
        }
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            containerColor = Color(0xF405160C),
            modifier = Modifier.background(Color(0xF405160C), RoundedCornerShape(12.dp))
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = Color.White) },
                    onClick = {
                        onCambio(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaDato(
    titulo: String,
    texto: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DoradoDinero.copy(alpha = 0.42f), RoundedCornerShape(8.dp))
            .background(Color(0x77040F08), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(titulo, color = DoradoDinero, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(texto, color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SeparadorMenu() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        color = DoradoDinero.copy(alpha = 0.24f)
    )
}

private fun preferenciasUsuario(context: Context) =
    context.getSharedPreferences("quri_user_menu", Context.MODE_PRIVATE)

private fun aviso(context: Context, texto: String) {
    Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
}




