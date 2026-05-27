package com.example.quritfg.datos.modelo

import com.example.quritfg.datos.local.MetaEntidad
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
        val ingreso = IngresoBancoDetectado(
            entidad = "Banco Quri Demo",
            concepto = "Nomina detectada",
            cantidadCentimos = ingresoDetectadoCentimos,
            fecha = FechaQuri.hoyTexto()
        )
        val necesidades = fondos.mapNotNull { fondo -> calcularNecesidad(fondo, hoy) }

        val maximoAsignable = reglas.maximoPorcentajeIngreso
            ?.let { ingresoDetectadoCentimos * it.coerceIn(1, 100) / 100 }
            ?: ingresoDetectadoCentimos
        val necesidadesConReglas = aplicarReglas(necesidades, ingresoDetectadoCentimos, reglas)
        val totalNecesario = necesidadesConReglas.sumOf { it.necesarioMensualCentimos }
        val repartos = when {
            necesidadesConReglas.isEmpty() -> emptyList()
            totalNecesario <= maximoAsignable -> necesidadesConReglas.map {
                it.copy(asignadoCentimos = it.necesarioMensualCentimos)
            }
            else -> repartirPorPrioridad(necesidadesConReglas, maximoAsignable, reglas.priorizarMetasUrgentes)
        }

        val totalAsignado = repartos.sumOf { it.asignadoCentimos }
        return PlanBancoDemo(
            ingreso = ingreso,
            repartos = repartos,
            totalAsignadoCentimos = totalAsignado,
            restanteDisponibleCentimos = (ingresoDetectadoCentimos - totalAsignado).coerceAtLeast(0L)
        )
    }

    private fun calcularNecesidad(fondo: MetaEntidad, hoy: LocalDate): RepartoFondoBanco? {
        val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
        if (restante == 0L) {
            return RepartoFondoBanco(
                fondo = fondo,
                mesesRestantes = 0L,
                necesarioMensualCentimos = 0L,
                asignadoCentimos = 0L,
                estado = EstadoFondoBanco.COMPLETADO
            )
        }

        val fechaLimite = runCatching { FechaQuri.parsear(fondo.fechaLimite) }.getOrDefault(hoy)
        val fechaVencida = fechaLimite.isBefore(hoy)
        val mesesRestantes = ChronoUnit.MONTHS.between(hoy.withDayOfMonth(1), fechaLimite.withDayOfMonth(1))
            .coerceAtLeast(1L)
        val necesarioMensual = dividirRedondeandoArriba(restante, mesesRestantes)

        return RepartoFondoBanco(
            fondo = fondo,
            mesesRestantes = mesesRestantes,
            necesarioMensualCentimos = necesarioMensual,
            asignadoCentimos = 0L,
            estado = if (fechaVencida) EstadoFondoBanco.VAS_TARDE else EstadoFondoBanco.VAS_BIEN
        )
    }

    private fun repartirPorPrioridad(
        necesidades: List<RepartoFondoBanco>,
        ingresoCentimos: Long,
        priorizarUrgentes: Boolean
    ): List<RepartoFondoBanco> {
        var restante = ingresoCentimos
        val comparador = if (priorizarUrgentes) {
            compareBy<RepartoFondoBanco> { it.mesesRestantes }.thenBy { it.fondo.prioridad }
        } else {
            compareBy<RepartoFondoBanco> { it.fondo.prioridad }.thenBy { it.fondo.fechaLimite }
        }
        return necesidades
            .sortedWith(comparador)
            .map { necesidad ->
                val asignado = necesidad.necesarioMensualCentimos.coerceAtMost(restante)
                restante -= asignado
                necesidad.copy(asignadoCentimos = asignado)
            }
            .sortedByDescending { it.fondo.id }
        }

    private fun aplicarReglas(
        necesidades: List<RepartoFondoBanco>,
        ingresoCentimos: Long,
        reglas: ReglasAutomaticasQuri
    ): List<RepartoFondoBanco> =
        necesidades.map { reparto ->
            val nombre = reparto.fondo.nombre.lowercase()
            val porcentaje = reglas.porcentajesPorNombre
                .firstNotNullOfOrNull { (clave, valor) -> valor.takeIf { nombre.contains(clave.lowercase()) } }
            val minimo = reglas.minimosPorNombreCentimos
                .firstNotNullOfOrNull { (clave, valor) -> valor.takeIf { nombre.contains(clave.lowercase()) } }
            val sugerenciaPorcentaje = porcentaje?.let { ingresoCentimos * it.coerceIn(0, 100) / 100 }
            val sugerencia = listOfNotNull(
                reparto.necesarioMensualCentimos,
                sugerenciaPorcentaje,
                minimo
            ).maxOrNull() ?: reparto.necesarioMensualCentimos
            reparto.copy(necesarioMensualCentimos = sugerencia)
        }

    private fun dividirRedondeandoArriba(cantidad: Long, divisor: Long): Long =
        (cantidad + divisor - 1L) / divisor
}
