package com.example.quritfg.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel que gestiona la creación de movimientos.
 * Puede crear tanto gastos como ingresos.
 */
class AnadirMovimientoViewModel(
    private val repositorio: RepositorioQuriRoom
) : ViewModel() {

    // Tipo de movimiento actual (GASTO o INGRESO)
    var tipoMovimiento by mutableStateOf(TipoMovimiento.GASTO)
        private set

    // Datos introducidos por el usuario
    var categoria by mutableStateOf("")
        private set

    var cantidad by mutableStateOf("")
        private set

    var etiquetaGasto by mutableStateOf(EtiquetaGasto.NECESARIO.texto)
        private set

    // Fecha generada automáticamente
    var fecha by mutableStateOf(LocalDate.now().toString())
        private set

    // Variables de error para validación
    var errorCategoria by mutableStateOf<String?>(null)
        private set

    var errorCantidad by mutableStateOf<String?>(null)
        private set

    // Indica si el formulario es válido
    val puedeGuardar: Boolean
        get() = errorCategoria == null &&
                errorCantidad == null &&
                categoria.isNotBlank() &&
                cantidad.isNotBlank()

    // Cambia el tipo de movimiento
    fun onTipoMovimientoChange(nuevo: TipoMovimiento) {
        if (tipoMovimiento != nuevo) {
            categoria = ""
            errorCategoria = null
        }

        tipoMovimiento = nuevo
        etiquetaGasto = EtiquetaGasto.NECESARIO.texto
    }

    // Valida el concepto o categoría
    fun onCategoriaChange(nuevo: String) {
        categoria = nuevo
        errorCategoria =
            if (nuevo.isBlank()) "El concepto es obligatorio"
            else null
    }

    // Valida la cantidad introducida
    fun onCantidadChange(nuevo: String) {
        cantidad = nuevo
        val valor = nuevo.toDoubleOrNull()

        errorCantidad = when {
            nuevo.isBlank() -> "La cantidad es obligatoria"
            valor == null -> "Introduce un número válido"
            valor <= 0.0 -> "Debe ser mayor que 0"
            else -> null
        }
    }

    fun onEtiquetaGastoChange(nueva: EtiquetaGasto) {
        etiquetaGasto = nueva.texto
    }

    /**
     * Guarda el movimiento en la base de datos.
     * Si es gasto, inserta en la tabla de gastos.
     * Si es ingreso, inserta en la tabla de ingresos.
     */
    fun guardarMovimiento() {
        val valor = cantidad.toDoubleOrNull() ?: return

        viewModelScope.launch {
            if (tipoMovimiento == TipoMovimiento.GASTO) {

                val gasto = GastoEntidad(
                    categoria = categoria.trim(),
                    cantidad = valor,
                    fecha = fecha,
                    etiqueta = etiquetaGasto
                )

                repositorio.anadirGasto(gasto)

            } else {

                val ingreso = IngresoEntidad(
                    cantidad = valor,
                    fecha = fecha,
                    concepto = categoria.trim()
                )

                repositorio.anadirIngreso(ingreso)
            }
        }

        // Limpia los campos después de guardar
        categoria = ""
        cantidad = ""
        etiquetaGasto = EtiquetaGasto.NECESARIO.texto
        fecha = LocalDate.now().toString()
        errorCategoria = null
        errorCantidad = null
    }
}
