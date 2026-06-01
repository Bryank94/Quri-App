package com.example.quritfg.datos.modelo

import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.shared.banco.BancoDemoComun
import com.example.quritfg.shared.banco.EstadoFondoBancoComun
import com.example.quritfg.shared.banco.ReglasAutomaticasQuriComun
import com.example.quritfg.shared.modelo.FechaQuriComun
import com.example.quritfg.shared.modelo.FondoAhorro
import java.time.LocalDate

data class IngresoBancoDetectado(
    val entidad: String,
    val concepto: String,
    val cantidadCentimos: Long,
    val fecha: String,
    val esDemo: Boolean = true
)

data class RepartoFondoBanco(
    val fondo: MetaEntidad,
    val mesesRestantes: Long,
    val necesarioMensualCentimos: Long,
    val asignadoCentimos: Long,
    val estado: EstadoFondoBanco
)

data class PlanBancoDemo(
    val ingreso: IngresoBancoDetectado,
    val repartos: List<RepartoFondoBanco>,
    val totalAsignadoCentimos: Long,
    val restanteDisponibleCentimos: Long
)

data class ReglasAutomaticasQuri(
    val porcentajesPorNombre: Map<String, Int> = emptyMap(),
    val minimosPorNombreCentimos: Map<String, Long> = emptyMap(),
    val maximoPorcentajeIngreso: Int? = null,
    val priorizarMetasUrgentes: Boolean = false
)

enum class EstadoFondoBanco {
    VAS_BIEN,
    VAS_TARDE,
    COMPLETADO
}

object BancoDemo {
    private const val NOMINA_DEMO_CENTIMOS = 130_000L

    fun planificarReparto(
        fondos: List<MetaEntidad>,
        ingresoDetectadoCentimos: Long = NOMINA_DEMO_CENTIMOS,
        hoy: LocalDate = LocalDate.now(),
        reglas: ReglasAutomaticasQuri = ReglasAutomaticasQuri()
    ): PlanBancoDemo {
        val planComun = BancoDemoComun.planificarReparto(
            fondos = fondos.map { it.aFondoAhorro() },
            ingresoDetectadoCentimos = ingresoDetectadoCentimos,
            hoy = hoy.aFechaQuriComun(),
            reglas = reglas.aReglasComunes()
        )
        return PlanBancoDemo(
            ingreso = IngresoBancoDetectado(
                entidad = planComun.ingreso.entidad,
                concepto = planComun.ingreso.concepto,
                cantidadCentimos = planComun.ingreso.cantidadCentimos,
                fecha = planComun.ingreso.fecha,
                esDemo = planComun.ingreso.esDemo
            ),
            repartos = planComun.repartos.map { repartoComun ->
                RepartoFondoBanco(
                    fondo = fondos.encontrarFondo(repartoComun.fondo),
                    mesesRestantes = repartoComun.mesesRestantes,
                    necesarioMensualCentimos = repartoComun.necesarioMensualCentimos,
                    asignadoCentimos = repartoComun.asignadoCentimos,
                    estado = repartoComun.estado.aEstadoAndroid()
                )
            },
            totalAsignadoCentimos = planComun.totalAsignadoCentimos,
            restanteDisponibleCentimos = planComun.restanteDisponibleCentimos
        )
    }

    private fun MetaEntidad.aFondoAhorro(): FondoAhorro =
        FondoAhorro(
            id = id,
            nombre = nombre,
            cantidadObjetivoCentimos = cantidadObjetivoCentimos,
            cantidadActualCentimos = cantidadActualCentimos,
            fechaLimite = fechaLimite,
            prioridad = prioridad
        )

    private fun ReglasAutomaticasQuri.aReglasComunes(): ReglasAutomaticasQuriComun =
        ReglasAutomaticasQuriComun(
            porcentajesPorNombre = porcentajesPorNombre,
            minimosPorNombreCentimos = minimosPorNombreCentimos,
            maximoPorcentajeIngreso = maximoPorcentajeIngreso,
            priorizarMetasUrgentes = priorizarMetasUrgentes
        )

    private fun LocalDate.aFechaQuriComun(): FechaQuriComun =
        FechaQuriComun(year = year, month = monthValue, day = dayOfMonth)

    private fun EstadoFondoBancoComun.aEstadoAndroid(): EstadoFondoBanco =
        when (this) {
            EstadoFondoBancoComun.VAS_BIEN -> EstadoFondoBanco.VAS_BIEN
            EstadoFondoBancoComun.VAS_TARDE -> EstadoFondoBanco.VAS_TARDE
            EstadoFondoBancoComun.COMPLETADO -> EstadoFondoBanco.COMPLETADO
        }

    private fun List<MetaEntidad>.encontrarFondo(fondo: FondoAhorro): MetaEntidad =
        firstOrNull { it.id == fondo.id && it.nombre == fondo.nombre }
            ?: firstOrNull { it.id == fondo.id }
            ?: first { it.nombre == fondo.nombre }
}
