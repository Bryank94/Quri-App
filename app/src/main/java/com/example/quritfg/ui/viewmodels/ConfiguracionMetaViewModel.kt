package com.example.quritfg.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.analytics.AnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.launch
import java.time.LocalDate

class ConfiguracionMetaViewModel(
    private val repositorio: RepositorioQuriRoom,
    private val analytics: AnalyticsTracker? = null
) : ViewModel() {

    var nombreFondo by mutableStateOf("")
        private set

    var cantidadObjetivo by mutableStateOf("")
        private set

    var ahorroActual by mutableStateOf("")
        private set

    var fechaLimite by mutableStateOf("")
        private set

    var prioridad by mutableStateOf(2)
        private set

    var errorNombre by mutableStateOf<String?>(null)
        private set

    var errorCantidad by mutableStateOf<String?>(null)
        private set

    var errorAhorroActual by mutableStateOf<String?>(null)
        private set

    var errorFechaLimite by mutableStateOf<String?>(null)
        private set

    val puedeGuardar: Boolean
        get() = errorNombre == null &&
                errorCantidad == null &&
                errorAhorroActual == null &&
                errorFechaLimite == null &&
                nombreFondo.isNotBlank() &&
                cantidadObjetivo.isNotBlank() &&
                fechaLimite.isNotBlank()

    fun onNombreChange(nuevo: String) {
        nombreFondo = nuevo
        errorNombre = if (nuevo.isBlank()) "El nombre es obligatorio" else null
    }

    fun onCantidadObjetivoChange(nuevo: String) {
        cantidadObjetivo = nuevo
        val value = textoACentimos(nuevo)

        errorCantidad = when {
            nuevo.isBlank() -> "La cantidad es obligatoria"
            value == null -> "Introduce un numero valido"
            value <= 0L -> "Debe ser mayor que 0"
            else -> null
        }
    }

    fun onAhorroActualChange(nuevo: String) {
        ahorroActual = nuevo
        val value = textoACentimos(nuevo)

        errorAhorroActual = when {
            nuevo.isBlank() -> null
            value == null -> "Introduce un numero valido"
            value < 0L -> "No puede ser negativo"
            else -> null
        }
    }

    fun onPrioridadChange(nueva: Int) {
        prioridad = nueva.coerceIn(1, 3)
    }

    fun onFechaLimiteChange(nuevo: String) {
        fechaLimite = nuevo
        errorFechaLimite = validarFechaLimite(nuevo)
    }

    fun guardarMetaInicial() {
        val objetivo = textoACentimos(cantidadObjetivo) ?: return
        val actual = textoACentimos(ahorroActual).takeIf { ahorroActual.isNotBlank() } ?: 0L

        val meta = MetaEntidad(
            nombre = nombreFondo.trim(),
            cantidadObjetivoCentimos = objetivo,
            cantidadActualCentimos = actual.coerceAtMost(objetivo),
            fechaLimite = fechaLimite.trim(),
            prioridad = prioridad
        )

        viewModelScope.launch {
            repositorio.guardarMeta(meta)
            analytics?.track(QuriAnalyticsEvents.FIRST_GOAL_CREATED, mapOf("priority" to prioridad.toString()))
        }
    }

    private fun validarFechaLimite(fecha: String): String? {
        if (fecha.isBlank()) return "La fecha limite es obligatoria"

        val fechaParseada = try {
            FechaQuri.parsear(fecha)
        } catch (_: Exception) {
            return "Usa el formato dd-MM-yyyy"
        }

        return if (fechaParseada.isBefore(LocalDate.now())) {
            "La fecha limite no puede estar en el pasado"
        } else {
            null
        }
    }
}
