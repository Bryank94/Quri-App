package com.example.quritfg.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quritfg.datos.analytics.AnalyticsTracker
import com.example.quritfg.datos.analytics.QuriAnalyticsEvents
import com.example.quritfg.datos.local.IngresoDetectadoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.local.RepartoDetectadoEntidad
import com.example.quritfg.datos.modelo.BancoDemo
import com.example.quritfg.datos.modelo.FechaQuri
import com.example.quritfg.datos.modelo.PlanBancoDemo
import com.example.quritfg.datos.modelo.ReglasAutomaticasQuri
import com.example.quritfg.datos.modelo.centimosAEuros
import com.example.quritfg.datos.modelo.textoACentimos
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SimulacionBancariaViewModel(
    private val repositorio: RepositorioQuriRoom,
    context: Context? = null,
    private val analytics: AnalyticsTracker? = null
) : ViewModel() {
    private val reglasPrefs = context?.applicationContext
        ?.getSharedPreferences("quri_reglas_automaticas", Context.MODE_PRIVATE)

    val fondos = repositorio.obtenerFondos()
    val historial: Flow<List<IngresoDetectadoEntidad>> = repositorio.obtenerIngresosDetectados()

    var importeNomina = mutableStateOf("1300")
        private set

    var plan = mutableStateOf<PlanBancoDemo?>(null)
        private set

    var mensaje = mutableStateOf<String?>(null)
        private set

    val repartosEditables = mutableStateMapOf<Int, String>()

    var reglasAutomaticasActivas = mutableStateOf(reglasPrefs?.getBoolean("reglas_activas", true) ?: true)
        private set

    var porcentajeViaje = mutableStateOf(reglasPrefs?.getInt("viaje_porcentaje", 15)?.toString() ?: "15")
        private set

    var minimoEmergencia = mutableStateOf(reglasPrefs?.getString("emergencia_minimo", "100") ?: "100")
        private set

    var maximoIngreso = mutableStateOf(reglasPrefs?.getInt("maximo_ingreso", 40)?.toString() ?: "40")
        private set

    var priorizarUrgentes = mutableStateOf(reglasPrefs?.getBoolean("priorizar_urgentes", true) ?: true)
        private set

    fun onImporteNominaChange(nuevo: String) {
        importeNomina.value = nuevo
        mensaje.value = null
    }

    fun onReglasAutomaticasChange(activo: Boolean) {
        reglasAutomaticasActivas.value = activo
        reglasPrefs?.edit()?.putBoolean("reglas_activas", activo)?.apply()
        mensaje.value = null
    }

    fun onPorcentajeViajeChange(nuevo: String) {
        porcentajeViaje.value = nuevo
        reglasPrefs?.edit()?.putInt("viaje_porcentaje", nuevo.toIntOrNull() ?: 15)?.apply()
    }

    fun onMinimoEmergenciaChange(nuevo: String) {
        minimoEmergencia.value = nuevo
        reglasPrefs?.edit()?.putString("emergencia_minimo", nuevo)?.apply()
    }

    fun onMaximoIngresoChange(nuevo: String) {
        maximoIngreso.value = nuevo
        reglasPrefs?.edit()?.putInt("maximo_ingreso", nuevo.toIntOrNull() ?: 40)?.apply()
    }

    fun onPriorizarUrgentesChange(activo: Boolean) {
        priorizarUrgentes.value = activo
        reglasPrefs?.edit()?.putBoolean("priorizar_urgentes", activo)?.apply()
    }

    fun simular(fondosActuales: List<MetaEntidad>) {
        val importe = textoACentimos(importeNomina.value)
        if (importe == null || importe <= 0L) {
            mensaje.value = "Introduce una nomina valida"
            return
        }

        val nuevoPlan = BancoDemo.planificarReparto(
            fondos = fondosActuales,
            ingresoDetectadoCentimos = importe,
            reglas = if (reglasAutomaticasActivas.value) reglasBasicas() else ReglasAutomaticasQuri()
        )
        plan.value = nuevoPlan
        repartosEditables.clear()
        nuevoPlan.repartos.forEach { reparto ->
            repartosEditables[reparto.fondo.id] = centimosAEuros(reparto.asignadoCentimos).toPlainString()
        }
        mensaje.value = "Nomina simulada y reparto preparado"
        analytics?.track(
            QuriAnalyticsEvents.FIRST_SALARY_SIMULATED,
            mapOf("amount_centimos" to importe.toString(), "funds" to fondosActuales.size.toString())
        )
    }

    fun onRepartoChange(fondoId: Int, valor: String) {
        repartosEditables[fondoId] = valor
        mensaje.value = null
        analytics?.track(QuriAnalyticsEvents.ALLOCATION_EDITED, mapOf("fund_id" to fondoId.toString()))
    }

    fun confirmar() {
        val planActual = plan.value ?: return
        val cantidadIngreso = textoACentimos(importeNomina.value) ?: return
        val repartosCentimos = repartosEditables.mapValues { (_, valor) ->
            textoACentimos(valor) ?: 0L
        }.filterValues { it > 0L }

        val totalAsignado = repartosCentimos.values.sum()
        if (totalAsignado > cantidadIngreso) {
            mensaje.value = "El reparto supera la nomina detectada"
            return
        }

        viewModelScope.launch {
            val resumen = planActual.repartos.joinToString(" | ") { reparto ->
                "${reparto.fondo.nombre}: ${repartosEditables[reparto.fondo.id] ?: "0"}"
            }
            repositorio.confirmarIngresoDetectado(
                ingreso = IngresoEntidad(
                    cantidadCentimos = cantidadIngreso,
                    fecha = FechaQuri.hoyTexto(),
                    concepto = planActual.ingreso.concepto
                ),
                ingresoDetectado = IngresoDetectadoEntidad(
                    concepto = "Nomina ${mesActual()} detectada",
                    entidad = planActual.ingreso.entidad,
                    cantidadCentimos = cantidadIngreso,
                    fecha = FechaQuri.hoyTexto(),
                    repartoResumen = resumen,
                    totalAsignadoCentimos = totalAsignado,
                    confirmado = true
                ),
                repartos = planActual.repartos.mapNotNull { reparto ->
                    val cantidad = repartosCentimos[reparto.fondo.id] ?: return@mapNotNull null
                    RepartoDetectadoEntidad(
                        ingresoDetectadoId = 0,
                        fondoId = reparto.fondo.id,
                        fondoNombre = reparto.fondo.nombre,
                        cantidadCentimos = cantidad
                    )
                }
            )
            mensaje.value = "Reparto confirmado y fondos actualizados"
            analytics?.track(
                QuriAnalyticsEvents.ALLOCATION_CONFIRMED,
                mapOf("amount_centimos" to cantidadIngreso.toString(), "assigned_centimos" to totalAsignado.toString())
            )
        }
    }

    fun deshacer(ingreso: IngresoDetectadoEntidad) {
        viewModelScope.launch {
            repositorio.deshacerReparto(ingreso)
            mensaje.value = "Reparto deshecho y fondos restaurados"
            analytics?.track(QuriAnalyticsEvents.UNDO_USED, mapOf("income_id" to ingreso.id.toString()))
        }
    }

    private fun reglasBasicas(): ReglasAutomaticasQuri =
        ReglasAutomaticasQuri(
            porcentajesPorNombre = mapOf("viaje" to (porcentajeViaje.value.toIntOrNull() ?: 15)),
            minimosPorNombreCentimos = mapOf("emergencia" to (textoACentimos(minimoEmergencia.value) ?: 100_00L)),
            maximoPorcentajeIngreso = maximoIngreso.value.toIntOrNull() ?: 40,
            priorizarMetasUrgentes = priorizarUrgentes.value
        )

    private fun mesActual(): String {
        val meses = listOf(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )
        return meses[java.time.LocalDate.now().monthValue - 1]
    }
}

class SimulacionBancariaViewModelFactory(
    private val repositorio: RepositorioQuriRoom,
    private val context: Context? = null,
    private val analytics: AnalyticsTracker? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimulacionBancariaViewModel::class.java)) {
            return SimulacionBancariaViewModel(repositorio, context, analytics) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
