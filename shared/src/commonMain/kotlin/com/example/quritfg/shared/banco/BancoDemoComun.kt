package com.example.quritfg.shared.banco

import com.example.quritfg.shared.modelo.FechaQuriComun
import com.example.quritfg.shared.modelo.FechaQuriComunParser
import com.example.quritfg.shared.modelo.FondoAhorro

data class IngresoBancoDetectadoComun(
    val entidad: String,
    val concepto: String,
    val cantidadCentimos: Long,
    val fecha: String,
    val esDemo: Boolean = true
)

data class RepartoFondoBancoComun(
    val fondo: FondoAhorro,
    val mesesRestantes: Long,
    val necesarioMensualCentimos: Long,
    val asignadoCentimos: Long,
    val estado: EstadoFondoBancoComun
)

data class PlanBancoDemoComun(
    val ingreso: IngresoBancoDetectadoComun,
    val repartos: List<RepartoFondoBancoComun>,
    val totalAsignadoCentimos: Long,
    val restanteDisponibleCentimos: Long
)

data class ReglasAutomaticasQuriComun(
    val porcentajesPorNombre: Map<String, Int> = emptyMap(),
    val minimosPorNombreCentimos: Map<String, Long> = emptyMap(),
    val maximoPorcentajeIngreso: Int? = null,
    val priorizarMetasUrgentes: Boolean = false
)

enum class EstadoFondoBancoComun {
    VAS_BIEN,
    VAS_TARDE,
    COMPLETADO
}

object BancoDemoComun {
    private const val NOMINA_DEMO_CENTIMOS = 130_000L

    fun planificarReparto(
        fondos: List<FondoAhorro>,
        ingresoDetectadoCentimos: Long = NOMINA_DEMO_CENTIMOS,
        hoy: FechaQuriComun,
        reglas: ReglasAutomaticasQuriComun = ReglasAutomaticasQuriComun()
    ): PlanBancoDemoComun {
        val ingreso = IngresoBancoDetectadoComun(
            entidad = "Banco Quri Demo",
            concepto = "Nomina detectada",
            cantidadCentimos = ingresoDetectadoCentimos,
            fecha = hoy.mostrarUsuario()
        )
        val necesidades = fondos.map { calcularNecesidad(it, hoy) }
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
        return PlanBancoDemoComun(
            ingreso = ingreso,
            repartos = repartos,
            totalAsignadoCentimos = totalAsignado,
            restanteDisponibleCentimos = (ingresoDetectadoCentimos - totalAsignado).coerceAtLeast(0L)
        )
    }

    private fun calcularNecesidad(fondo: FondoAhorro, hoy: FechaQuriComun): RepartoFondoBancoComun {
        val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
        if (restante == 0L) {
            return RepartoFondoBancoComun(
                fondo = fondo,
                mesesRestantes = 0L,
                necesarioMensualCentimos = 0L,
                asignadoCentimos = 0L,
                estado = EstadoFondoBancoComun.COMPLETADO
            )
        }

        val fechaLimite = FechaQuriComunParser.parsear(fondo.fechaLimite) ?: hoy
        val fechaVencida = fechaLimite < hoy
        val mesesRestantes = hoy.mesesHasta(fechaLimite)
        val necesarioMensual = dividirRedondeandoArriba(restante, mesesRestantes)

        return RepartoFondoBancoComun(
            fondo = fondo,
            mesesRestantes = mesesRestantes,
            necesarioMensualCentimos = necesarioMensual,
            asignadoCentimos = 0L,
            estado = if (fechaVencida) EstadoFondoBancoComun.VAS_TARDE else EstadoFondoBancoComun.VAS_BIEN
        )
    }

    private fun repartirPorPrioridad(
        necesidades: List<RepartoFondoBancoComun>,
        ingresoCentimos: Long,
        priorizarUrgentes: Boolean
    ): List<RepartoFondoBancoComun> {
        var restante = ingresoCentimos
        val comparador = if (priorizarUrgentes) {
            compareBy<RepartoFondoBancoComun> { it.mesesRestantes }.thenBy { it.fondo.prioridad }
        } else {
            compareBy<RepartoFondoBancoComun> { it.fondo.prioridad }.thenBy { it.fondo.fechaLimite }
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
        necesidades: List<RepartoFondoBancoComun>,
        ingresoCentimos: Long,
        reglas: ReglasAutomaticasQuriComun
    ): List<RepartoFondoBancoComun> =
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
