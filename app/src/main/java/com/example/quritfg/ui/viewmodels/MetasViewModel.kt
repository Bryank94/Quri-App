package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel proporciona los fondos almacenados.
 */
class MetasViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // contiene los fondos guardados en la base de datos
    val fondos: Flow<List<MetaEntidad>> =
        repositorio.obtenerFondos()

    val metaActual: Flow<MetaEntidad?> =
        repositorio.obtenerMeta()

    val gastos =
        repositorio.obtenerGastos()

    fun actualizarFondo(
        fondo: MetaEntidad,
        nombre: String,
        objetivo: String,
        fechaLimite: String,
        prioridad: Int
    ): Boolean {
        val objetivoCentimos = textoACentimos(objetivo) ?: return false
        if (nombre.isBlank() || objetivoCentimos <= 0L || !fechaLimiteValida(fechaLimite)) return false

        viewModelScope.launch {
            repositorio.guardarMeta(
                fondo.copy(
                    nombre = nombre.trim(),
                    cantidadObjetivoCentimos = objetivoCentimos,
                    fechaLimite = fechaLimite.trim(),
                    prioridad = prioridad.coerceIn(1, 3)
                )
            )
        }

        return true
    }

    fun eliminarFondo(fondo: MetaEntidad) {
        viewModelScope.launch {
            repositorio.eliminarFondo(fondo.id)
        }
    }

    private fun fechaLimiteValida(fecha: String): Boolean =
        try {
            !FechaQuri.parsear(fecha).isBefore(LocalDate.now())
        } catch (_: Exception) {
            false
        }
}

