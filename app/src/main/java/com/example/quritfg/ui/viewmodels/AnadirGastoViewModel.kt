package com.example.quritfg.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.centimosAEuros
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AnadirMovimientoViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    var tipoMovimiento by mutableStateOf(TipoMovimiento.GASTO)
        private set

    val fondos: Flow<List<MetaEntidad>> =
        repositorio.obtenerFondos()

    var categoria by mutableStateOf("")
        private set

    var cantidad by mutableStateOf("")
        private set

    var etiquetaGasto by mutableStateOf(EtiquetaGasto.NECESARIO.texto)
        private set

    val aportacionesFondos = mutableStateMapOf<Int, String>()

    var fondoDistribucionSeleccionadoId by mutableStateOf<Int?>(null)
        private set

    var cantidadDistribucion by mutableStateOf("")
        private set

    var fecha by mutableStateOf(FechaQuri.hoyTexto())
        private set

    var errorCategoria by mutableStateOf<String?>(null)
        private set

    var errorCantidad by mutableStateOf<String?>(null)
        private set

    var errorAportaciones by mutableStateOf<String?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    var guardadoCorrectamente by mutableStateOf(false)
        private set

    var errorGuardado by mutableStateOf<String?>(null)
        private set

    val puedeGuardar: Boolean
        get() = errorCategoria == null &&
                errorCantidad == null &&
                errorAportaciones == null &&
                categoria.isNotBlank() &&
                cantidad.isNotBlank() &&
                !guardando

    val puedeAnadirAportacion: Boolean
        get() = fondoDistribucionSeleccionadoId != null &&
                (textoACentimos(cantidadDistribucion) ?: 0L) > 0L

    fun onTipoMovimientoChange(nuevo: TipoMovimiento) {
        if (tipoMovimiento != nuevo) {
            categoria = ""
            errorCategoria = null
        }

        tipoMovimiento = nuevo
        etiquetaGasto = EtiquetaGasto.NECESARIO.texto
        aportacionesFondos.clear()
        fondoDistribucionSeleccionadoId = null
        cantidadDistribucion = ""
        errorAportaciones = null
    }

    fun onCategoriaChange(nuevo: String) {
        categoria = nuevo
        errorCategoria = if (nuevo.isBlank()) "El concepto es obligatorio" else null
    }

    fun onCantidadChange(nuevo: String) {
        cantidad = nuevo
        val valor = textoACentimos(nuevo)
        guardadoCorrectamente = false
        errorGuardado = null

        errorCantidad = when {
            nuevo.isBlank() -> "La cantidad es obligatoria"
            valor == null -> "Introduce un numero valido"
            valor <= 0L -> "Debe ser mayor que 0"
            else -> null
        }
        validarAportaciones()
    }

    fun onEtiquetaGastoChange(nueva: EtiquetaGasto) {
        etiquetaGasto = nueva.texto
    }

    fun onAportacionFondoChange(fondoId: Int, nuevaCantidad: String) {
        if (nuevaCantidad.isBlank()) {
            aportacionesFondos.remove(fondoId)
        } else {
            aportacionesFondos[fondoId] = nuevaCantidad
        }
        validarAportaciones()
    }

    fun onFondoDistribucionChange(fondoId: Int?) {
        fondoDistribucionSeleccionadoId = fondoId
    }

    fun onCantidadDistribucionChange(nuevaCantidad: String) {
        cantidadDistribucion = nuevaCantidad
    }

    fun anadirAportacionSeleccionada() {
        val fondoId = fondoDistribucionSeleccionadoId ?: return
        val cantidadCentimos = textoACentimos(cantidadDistribucion) ?: return
        if (cantidadCentimos <= 0L) return

        aportacionesFondos[fondoId] = cantidadDistribucion
        fondoDistribucionSeleccionadoId = null
        cantidadDistribucion = ""
        validarAportaciones()
    }

    fun distribuirAutomaticamente(fondos: List<MetaEntidad>) {
        val totalIngreso = textoACentimos(cantidad) ?: return
        if (tipoMovimiento != TipoMovimiento.INGRESO || totalIngreso <= 0L) return

        aportacionesFondos.clear()
        val maximoAsignable = (totalIngreso * 40L) / 100L
        var disponible = maximoAsignable
        fondos
            .filter { it.cantidadActualCentimos < it.cantidadObjetivoCentimos }
            .sortedWith(compareBy<MetaEntidad> { it.prioridad }.thenBy { it.fechaLimite })
            .forEach { fondo ->
                if (disponible <= 0L) return@forEach
                val restanteFondo = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
                val asignacion = minOf(restanteFondo, disponible)
                if (asignacion > 0L) {
                    aportacionesFondos[fondo.id] = centimosAEuros(asignacion).toPlainString()
                    disponible -= asignacion
                }
            }
        validarAportaciones()
    }
    fun quitarAportacion(fondoId: Int) {
        aportacionesFondos.remove(fondoId)
        validarAportaciones()
    }

    fun totalAportacionesCentimos(): Long =
        aportacionesFondos.values.sumOf { textoACentimos(it) ?: 0L }

    fun remanenteCentimos(): Long {
        val totalIngreso = textoACentimos(cantidad) ?: 0L
        return (totalIngreso - totalAportacionesCentimos()).coerceAtLeast(0L)
    }

    private fun validarAportaciones() {
        if (tipoMovimiento != TipoMovimiento.INGRESO) {
            errorAportaciones = null
            return
        }

        val totalIngreso = textoACentimos(cantidad)
        val cantidades = aportacionesFondos.values.filter { it.isNotBlank() }
        val hayCantidadInvalida = cantidades.any { textoACentimos(it) == null }
        val totalAportado = cantidades.sumOf { textoACentimos(it) ?: 0L }

        errorAportaciones = when {
            hayCantidadInvalida -> "Revisa las cantidades de los fondos"
            totalIngreso != null && totalAportado > totalIngreso -> "Las aportaciones no pueden superar el ingreso"
            else -> null
        }
    }

    fun guardarMovimiento() {
        val valorCentimos = textoACentimos(cantidad) ?: return
        if (!puedeGuardar) return

        guardando = true
        guardadoCorrectamente = false
        errorGuardado = null
        viewModelScope.launch {
            try {
                if (tipoMovimiento == TipoMovimiento.GASTO) {
                    val gasto = GastoEntidad(
                        categoria = categoria.trim(),
                        cantidadCentimos = valorCentimos,
                        fecha = fecha,
                        etiqueta = etiquetaGasto
                    )
                    repositorio.anadirGasto(gasto)
                } else {
                    val ingreso = IngresoEntidad(
                        cantidadCentimos = valorCentimos,
                        fecha = fecha,
                        concepto = categoria.trim()
                    )
                    repositorio.anadirIngreso(ingreso)

                    aportacionesFondos.forEach { (fondoId, cantidadTexto) ->
                        val aportacionCentimos = textoACentimos(cantidadTexto) ?: 0L
                        repositorio.aportarAFondo(fondoId, aportacionCentimos)
                    }
                }

                categoria = ""
                cantidad = ""
                etiquetaGasto = EtiquetaGasto.NECESARIO.texto
                aportacionesFondos.clear()
                fondoDistribucionSeleccionadoId = null
                cantidadDistribucion = ""
                fecha = FechaQuri.hoyTexto()
                errorCategoria = null
                errorCantidad = null
                errorAportaciones = null
                guardadoCorrectamente = true
            } catch (_: Exception) {
                errorGuardado = "No se pudo guardar el movimiento. Intentalo de nuevo."
            } finally {
                guardando = false
            }
        }
    }

    fun consumirGuardadoCorrecto() {
        guardadoCorrectamente = false
    }
}


