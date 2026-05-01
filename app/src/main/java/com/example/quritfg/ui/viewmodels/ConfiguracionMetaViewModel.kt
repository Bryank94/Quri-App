package com.example.quritfg.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la creación y validación de la meta de ahorro.
 */
class ConfiguracionMetaViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // Estados del formulario
    var nombreMeta by mutableStateOf("")
        private set

    var cantidadObjetivo by mutableStateOf("")
        private set

    // Estados de error para validación
    var errorNombre by mutableStateOf<String?>(null)
        private set

    var errorCantidad by mutableStateOf<String?>(null)
        private set

    // Indica si el formulario es válido
    val puedeGuardar: Boolean
        get() = errorNombre == null &&
                errorCantidad == null &&
                nombreMeta.isNotBlank() &&
                cantidadObjetivo.isNotBlank()

    // Valida el nombre de la meta
    fun onNombreChange(nuevo: String) {
        nombreMeta = nuevo
        errorNombre = if (nuevo.isBlank()) "El nombre es obligatorio" else null
    }

    // Valida la cantidad objetivo
    fun onCantidadObjetivoChange(nuevo: String) {
        cantidadObjetivo = nuevo
        val value = nuevo.toDoubleOrNull()

        errorCantidad = when {
            nuevo.isBlank() -> "La cantidad es obligatoria"
            value == null -> "Introduce un número válido"
            value <= 0.0 -> "Debe ser mayor que 0"
            else -> null
        }
    }

    /**
     * Crea la entidad Meta y la guarda en la base de datos.
     */
    fun guardarMetaInicial() {
        val objetivo = cantidadObjetivo.toDoubleOrNull() ?: return

        val meta = MetaEntidad(
            nombre = nombreMeta.trim(),
            cantidadObjetivo = objetivo,
            cantidadActual = 0.0
        )

        viewModelScope.launch {
            repositorio.guardarMeta(meta)
        }
    }
}