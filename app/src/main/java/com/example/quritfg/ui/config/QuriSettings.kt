package com.example.quritfg.ui.config

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.quritfg.datos.modelo.centimosAEuros
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class QuriSettings(context: Context) {
    private val prefs = context.getSharedPreferences("quri_user_menu", Context.MODE_PRIVATE)

    var idioma by mutableStateOf(prefs.getString(KEY_IDIOMA, "Espanol") ?: "Espanol")
        private set

    var moneda by mutableStateOf(prefs.getString(KEY_MONEDA, "EUR") ?: "EUR")
        private set

    var temaOscuro by mutableStateOf(prefs.getBoolean(KEY_TEMA_OSCURO, true))
        private set

    fun actualizarIdioma(nuevoIdioma: String) {
        idioma = nuevoIdioma
        prefs.edit().putString(KEY_IDIOMA, nuevoIdioma).apply()
    }

    fun actualizarMoneda(nuevaMoneda: String) {
        moneda = nuevaMoneda
        prefs.edit().putString(KEY_MONEDA, nuevaMoneda).apply()
    }

    fun actualizarTemaOscuro(oscuro: Boolean) {
        temaOscuro = oscuro
        prefs.edit().putBoolean(KEY_TEMA_OSCURO, oscuro).apply()
    }

    fun guardarConfiguracion(
        idioma: String,
        moneda: String,
        fecha: String,
        objetivoMensual: String,
        limiteGasto: String,
        temaOscuro: Boolean,
        redondeo: Boolean,
        ahorroAutomatico: Boolean,
        recordatorios: Boolean,
        alertasMetas: Boolean,
        pin: Boolean,
        dobleFactor: Boolean
    ) {
        this.idioma = idioma
        this.moneda = moneda
        this.temaOscuro = temaOscuro
        prefs.edit()
            .putString(KEY_IDIOMA, idioma)
            .putString(KEY_MONEDA, moneda)
            .putString("config_fecha", fecha)
            .putString("config_objetivo_mensual", objetivoMensual)
            .putString("config_limite_gasto", limiteGasto)
            .putBoolean(KEY_TEMA_OSCURO, temaOscuro)
            .putBoolean("config_redondeo", redondeo)
            .putBoolean("config_ahorro_auto", ahorroAutomatico)
            .putBoolean("config_recordatorios", recordatorios)
            .putBoolean("config_alertas_metas", alertasMetas)
            .putBoolean("config_pin", pin)
            .putBoolean("config_2fa", dobleFactor)
            .apply()
    }

    fun texto(es: String, en: String): String =
        if (idioma == "English") en else es

    fun traducirValor(valor: String): String {
        if (idioma != "English") return valor
        if (valor.startsWith("Estas gastando mucho en cosas innecesarias")) {
            return "You are spending too much on unnecessary things this month. Try reducing those expenses to save more."
        }
        if (valor.startsWith("Felicidades!")) {
            return "Congratulations! You are spending less than last month. That means more savings."
        }
        if (valor.startsWith("Este mes gastaste")) {
            return "This month you spent more than last month. Review unnecessary expenses and adjust where you can."
        }
        return when (valor) {
            "Comida" -> "Food"
            "Transporte" -> "Transport"
            "Ocio" -> "Leisure"
            "Hogar" -> "Home"
            "Salud" -> "Health"
            "Otros" -> "Other"
            "Salario" -> "Salary"
            "Pagos" -> "Payments"
            "Importante" -> "Important"
            "Necesario" -> "Necessary"
            "Innecesario" -> "Unnecessary"
            "Fecha no valida" -> "Invalid date"
            "Registra tus primeros gastos para empezar a ver consejos de ahorro." -> "Register your first expenses to start seeing savings tips."
            "Registra tus gastos para empezar a recibir consejos." -> "Register your expenses to start receiving tips."
            "Buen trabajo registrando tus gastos. Etiquetarlos te ayudara a detectar donde puedes ahorrar mas." -> "Good job registering your expenses. Tagging them helps you find where you can save more."
            "El concepto es obligatorio" -> "The concept is required"
            "La cantidad es obligatoria" -> "The amount is required"
            "Introduce un numero valido" -> "Enter a valid number"
            "Debe ser mayor que 0" -> "Must be greater than 0"
            "Revisa las cantidades de los fondos" -> "Check fund amounts"
            "Las aportaciones no pueden superar el ingreso" -> "Contributions cannot exceed income"
            "No se pudo guardar el movimiento. Intentalo de nuevo." -> "Could not save the movement. Try again."
            "El nombre es obligatorio" -> "The name is required"
            "La fecha limite es obligatoria" -> "The deadline is required"
            "Usa el formato dd-MM-yyyy" -> "Use the dd-MM-yyyy format"
            "La fecha limite no puede estar en el pasado" -> "The deadline cannot be in the past"
            "El correo es obligatorio" -> "Email is required"
            "El correo no es válido" -> "Email is not valid"
            "La contraseña es obligatoria" -> "Password is required"
            "Debe tener al menos 6 caracteres" -> "Must have at least 6 characters"
            else -> valor
        }
    }

    fun formatearDineroQuri(centimos: Long): String {
        val locale = when (moneda) {
            "USD" -> Locale.US
            "MXN" -> Locale("es", "MX")
            else -> Locale("es", "ES")
        }
        val formato = NumberFormat.getCurrencyInstance(locale)
        formato.currency = Currency.getInstance(moneda)
        return formato.format(centimosAEuros(centimos))
    }

    companion object {
        private const val KEY_IDIOMA = "config_idioma"
        private const val KEY_MONEDA = "config_moneda"
        private const val KEY_TEMA_OSCURO = "config_tema_oscuro"
    }
}

val LocalQuriSettings = compositionLocalOf<QuriSettings> {
    error("QuriSettings no esta disponible")
}

@Composable
fun rememberQuriSettings(context: Context): QuriSettings =
    remember(context) { QuriSettings(context) }

@Composable
fun quriTexto(es: String, en: String): String =
    LocalQuriSettings.current.texto(es, en)

@Composable
fun quriValor(valor: String): String =
    LocalQuriSettings.current.traducirValor(valor)

@Composable
fun formatearDineroQuri(centimos: Long): String =
    LocalQuriSettings.current.formatearDineroQuri(centimos)
