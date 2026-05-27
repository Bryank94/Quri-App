package com.example.quritfg.datos.modelo

import com.example.quritfg.datos.local.GastoEntidad
import com.example.quritfg.datos.local.IngresoEntidad
import com.example.quritfg.datos.local.MetaEntidad
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

enum class NivelHabitoQuri {
    POSITIVO,
    AVISO,
    RIESGO
}

enum class TipoInsightQuri {
    LIQUIDEZ,
    TENDENCIA,
    CATEGORIA,
    PREDICCION,
    GASTO_REPETIDO,
    ALERTA
}

data class RecomendacionHabitoQuri(
    val titulo: String,
    val detalle: String,
    val nivel: NivelHabitoQuri,
    val fondoId: Int? = null
)

data class InsightFinancieroQuri(
    val titulo: String,
    val detalle: String,
    val tipo: TipoInsightQuri,
    val nivel: NivelHabitoQuri,
    val metrica: String? = null,
    val explicacion: String? = null
)

data class InformeHabitoQuri(
    val recomendaciones: List<RecomendacionHabitoQuri>,
    val mensajeNotificacion: String,
    val insights: List<InsightFinancieroQuri> = emptyList(),
    val saludFinanciera: Int = 50,
    val resumenInteligente: String = "Quri necesita mas datos para entender tu ritmo financiero."
)

object MotorHabitoQuri {
    fun analizar(
        ingresos: List<IngresoEntidad>,
        gastos: List<GastoEntidad>,
        fondos: List<MetaEntidad>,
        hoy: LocalDate = LocalDate.now()
    ): InformeHabitoQuri {
        val mesActual = YearMonth.from(hoy)
        val mesAnterior = mesActual.minusMonths(1)
        val ingresosMes = ingresos.filter { perteneceAlMes(it.fecha, mesActual) }.sumOf { it.cantidadCentimos }
        val ingresosMesAnterior = ingresos.filter { perteneceAlMes(it.fecha, mesAnterior) }.sumOf { it.cantidadCentimos }
        val gastosMes = gastos.filter { perteneceAlMes(it.fecha, mesActual) }
        val gastosMesAnterior = gastos.filter { perteneceAlMes(it.fecha, mesAnterior) }
        val totalGastosMes = gastosMes.sumOf { it.cantidadCentimos }
        val totalGastosAnterior = gastosMesAnterior.sumOf { it.cantidadCentimos }
        val ahorroActual = fondos.sumOf { it.cantidadActualCentimos }
        val capacidadAhorroMes = ingresosMes - totalGastosMes
        val recomendaciones = mutableListOf<RecomendacionHabitoQuri>()

        recomendaciones += recomendacionBalance(ingresosMes, totalGastosMes)
        recomendaciones += recomendacionesFondos(fondos, hoy)
        recomendacionGastoInnecesario(gastosMes)?.let { recomendaciones += it }

        if (fondos.isEmpty()) {
            recomendaciones += RecomendacionHabitoQuri(
                titulo = "Crea tu primer fondo",
                detalle = "Quri necesita al menos un objetivo para calcular habitos de ahorro.",
                nivel = NivelHabitoQuri.AVISO
            )
        }

        if (ahorroActual <= 0L && fondos.isNotEmpty()) {
            recomendaciones += RecomendacionHabitoQuri(
                titulo = "Primer empujon de ahorro",
                detalle = "Empieza con una aportacion pequena para activar el seguimiento de tus fondos.",
                nivel = NivelHabitoQuri.AVISO
            )
        }

        val ordenadas = recomendaciones
            .distinctBy { it.titulo + it.fondoId }
            .sortedWith(compareBy<RecomendacionHabitoQuri> { prioridadNivel(it.nivel) }.thenBy { it.fondoId ?: Int.MAX_VALUE })

        val insights = generarInsights(
            ingresosMes = ingresosMes,
            ingresosMesAnterior = ingresosMesAnterior,
            gastosMes = gastosMes,
            gastosMesAnterior = gastosMesAnterior,
            totalGastosMes = totalGastosMes,
            totalGastosAnterior = totalGastosAnterior,
            fondos = fondos,
            hoy = hoy
        ) + prediccionesFondos(fondos, capacidadAhorroMes, hoy) + detectarGastosRepetidos(gastos, mesActual)
        val salud = calcularSaludFinanciera(ingresosMes, totalGastosMes, capacidadAhorroMes, fondos, gastosMes, hoy)
        val resumen = resumenInteligente(salud, capacidadAhorroMes, insights)

        val mensaje = ordenadas.firstOrNull()?.let { "${it.titulo}: ${it.detalle}" }
            ?: insights.firstOrNull()?.let { "${it.titulo}: ${it.detalle}" }
            ?: "Tu plan mensual esta estable. Revisa Quri para mantener el ritmo."

        return InformeHabitoQuri(
            recomendaciones = ordenadas,
            mensajeNotificacion = mensaje,
            insights = insights,
            saludFinanciera = salud,
            resumenInteligente = resumen
        )
    }

    private fun recomendacionBalance(ingresosMes: Long, gastosMes: Long): RecomendacionHabitoQuri =
        when {
            ingresosMes <= 0L -> RecomendacionHabitoQuri(
                titulo = "Registra tu ingreso mensual",
                detalle = "Anade tu salario para que Quri calcule cuanto puedes repartir a fondos.",
                nivel = NivelHabitoQuri.AVISO
            )
            gastosMes > ingresosMes -> RecomendacionHabitoQuri(
                titulo = "Gastos por encima del ingreso",
                detalle = "Este mes estas gastando mas de lo que entra. Revisa gastos antes de reforzar fondos.",
                nivel = NivelHabitoQuri.RIESGO
            )
            gastosMes >= ingresosMes * 80 / 100 -> RecomendacionHabitoQuri(
                titulo = "Margen mensual ajustado",
                detalle = "Tus gastos consumen mas del 80% del ingreso. Conviene proteger los fondos prioritarios.",
                nivel = NivelHabitoQuri.AVISO
            )
            else -> RecomendacionHabitoQuri(
                titulo = "Buen margen de ahorro",
                detalle = "Tus ingresos cubren los gastos del mes. Puedes mantener o aumentar el reparto.",
                nivel = NivelHabitoQuri.POSITIVO
            )
        }

    private fun generarInsights(
        ingresosMes: Long,
        ingresosMesAnterior: Long,
        gastosMes: List<GastoEntidad>,
        gastosMesAnterior: List<GastoEntidad>,
        totalGastosMes: Long,
        totalGastosAnterior: Long,
        fondos: List<MetaEntidad>,
        hoy: LocalDate
    ): List<InsightFinancieroQuri> {
        val insights = mutableListOf<InsightFinancieroQuri>()
        val capacidadAhorro = ingresosMes - totalGastosMes
        val necesarioFondos = fondos.sumOf { necesarioMensual(it, hoy) }

        if (ingresosMes <= 0L) {
            insights += InsightFinancieroQuri(
                titulo = "Sin ingreso mensual detectado",
                detalle = "Registra una nomina o ingreso para que Quri pueda predecir tu liquidez.",
                tipo = TipoInsightQuri.LIQUIDEZ,
                nivel = NivelHabitoQuri.AVISO
            )
        } else if (capacidadAhorro < 0L) {
            insights += InsightFinancieroQuri(
                titulo = "Liquidez negativa",
                detalle = "Este mes te faltan ${formatearDinero(-capacidadAhorro)} para cubrir gastos sin tocar tus fondos.",
                tipo = TipoInsightQuri.LIQUIDEZ,
                nivel = NivelHabitoQuri.RIESGO,
                metrica = formatearDinero(capacidadAhorro),
                explicacion = "Quri resta tus gastos del mes a tus ingresos registrados."
            )
        } else if (necesarioFondos > 0L && capacidadAhorro < necesarioFondos) {
            insights += InsightFinancieroQuri(
                titulo = "Fondos bajo presion",
                detalle = "Tu margen libre es ${formatearDinero(capacidadAhorro)} y tus metas necesitan ${formatearDinero(necesarioFondos)}/mes.",
                tipo = TipoInsightQuri.PREDICCION,
                nivel = NivelHabitoQuri.RIESGO,
                metrica = formatearDinero(necesarioFondos - capacidadAhorro),
                explicacion = "Quri compara tu margen libre con lo que necesitan tus fondos cada mes."
            )
        } else if (necesarioFondos > 0L && capacidadAhorro >= necesarioFondos + 50_00) {
            insights += InsightFinancieroQuri(
                titulo = "Puedes adelantar metas",
                detalle = "Despues de gastos y ritmo de fondos te quedan ${formatearDinero(capacidadAhorro - necesarioFondos)} de margen.",
                tipo = TipoInsightQuri.PREDICCION,
                nivel = NivelHabitoQuri.POSITIVO,
                metrica = formatearDinero(capacidadAhorro - necesarioFondos),
                explicacion = "Quri calcula el dinero que sobra tras cubrir gastos y ritmo recomendado de fondos."
            )
        }

        if (totalGastosAnterior > 0L) {
            val variacion = ((totalGastosMes - totalGastosAnterior).toDouble() / totalGastosAnterior.toDouble()) * 100.0
            when {
                variacion >= 20.0 -> insights += InsightFinancieroQuri(
                    titulo = "Gasto mensual acelerado",
                    detalle = "Estas gastando ${formatearPorcentaje(variacion)}% mas que el mes anterior.",
                    tipo = TipoInsightQuri.TENDENCIA,
                    nivel = NivelHabitoQuri.RIESGO,
                    metrica = "+${formatearPorcentaje(variacion)}%",
                    explicacion = "Quri compara el total de gastos de este mes con el mes anterior."
                )
                variacion <= -15.0 -> insights += InsightFinancieroQuri(
                    titulo = "Mejora de gasto mensual",
                    detalle = "Has reducido tus gastos ${formatearPorcentaje(-variacion)}% frente al mes anterior.",
                    tipo = TipoInsightQuri.TENDENCIA,
                    nivel = NivelHabitoQuri.POSITIVO,
                    metrica = "-${formatearPorcentaje(-variacion)}%",
                    explicacion = "Quri compara el total de gastos de este mes con el mes anterior."
                )
            }
        }

        categoriaConMayorSubida(gastosMes, gastosMesAnterior)?.let { (categoria, subidaCentimos, subidaPorcentaje) ->
            insights += InsightFinancieroQuri(
                titulo = "Ojo con $categoria",
                detalle = "Esta categoria subio ${formatearDinero(subidaCentimos)} (${formatearPorcentaje(subidaPorcentaje)}%) frente al mes anterior.",
                tipo = TipoInsightQuri.CATEGORIA,
                nivel = if (subidaPorcentaje >= 50.0) NivelHabitoQuri.RIESGO else NivelHabitoQuri.AVISO,
                metrica = "+${formatearDinero(subidaCentimos)}",
                explicacion = "Quri agrupa tus gastos por categoria y busca subidas relevantes."
            )
        }

        val innecesario = gastosMes.filter { it.etiqueta.equals("Innecesario", ignoreCase = true) }.sumOf { it.cantidadCentimos }
        if (totalGastosMes > 0L && innecesario * 100 / totalGastosMes >= 25) {
            insights += InsightFinancieroQuri(
                titulo = "Margen recuperable",
                detalle = "Si reduces parte del gasto innecesario, puedes acelerar tus fondos sin aumentar ingresos.",
                tipo = TipoInsightQuri.CATEGORIA,
                nivel = NivelHabitoQuri.AVISO,
                metrica = formatearDinero(innecesario),
                explicacion = "Quri suma los gastos marcados como innecesarios este mes."
            )
        }

        return insights
            .distinctBy { it.titulo + it.tipo }
            .sortedWith(compareBy<InsightFinancieroQuri> { prioridadNivel(it.nivel) }.thenBy { it.tipo.ordinal })
            .take(5)
    }

    private fun prediccionesFondos(
        fondos: List<MetaEntidad>,
        capacidadAhorroMes: Long,
        hoy: LocalDate
    ): List<InsightFinancieroQuri> {
        if (capacidadAhorroMes <= 0L) return emptyList()
        return fondos.mapNotNull { fondo ->
            val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
            if (restante <= 0L) return@mapNotNull null
            val limite = runCatching { FechaQuri.parsear(fondo.fechaLimite) }.getOrNull() ?: return@mapNotNull null
            val aportacionEstimada = estimarAportacionMensual(fondo, fondos, capacidadAhorroMes, hoy)
            if (aportacionEstimada <= 0L) return@mapNotNull null
            val mesesNecesarios = ((restante + aportacionEstimada - 1L) / aportacionEstimada).coerceAtLeast(1L)
            val fechaEstimada = hoy.plusMonths(mesesNecesarios)
            val diferenciaDias = ChronoUnit.DAYS.between(limite, fechaEstimada)

            when {
                fechaEstimada.isAfter(limite) -> InsightFinancieroQuri(
                    titulo = "${fondo.nombre}: no llegas a tiempo",
                    detalle = "Con tu ritmo actual llegarias el ${FechaQuri.mostrar(fechaEstimada.toString())}, unos $diferenciaDias dias tarde.",
                    tipo = TipoInsightQuri.PREDICCION,
                    nivel = NivelHabitoQuri.RIESGO,
                    metrica = "+${formatearDinero((restante / mesesHasta(hoy, limite)).coerceAtLeast(0L))}/mes",
                    explicacion = "Quri reparte tu margen libre segun prioridad y compara la fecha estimada con la fecha limite."
                )
                fechaEstimada.plusDays(20).isAfter(limite) -> InsightFinancieroQuri(
                    titulo = "${fondo.nombre}: muy justo",
                    detalle = "Llegarias cerca de la fecha limite. Mantener el reparto mensual es importante.",
                    tipo = TipoInsightQuri.PREDICCION,
                    nivel = NivelHabitoQuri.AVISO,
                    metrica = FechaQuri.mostrar(fechaEstimada.toString()),
                    explicacion = "Quri estima cuantos meses faltan dividiendo lo pendiente entre tu aportacion posible."
                )
                else -> InsightFinancieroQuri(
                    titulo = "${fondo.nombre}: llegas a tiempo",
                    detalle = "Si mantienes el ritmo actual, podrias completar este fondo antes de la fecha limite.",
                    tipo = TipoInsightQuri.PREDICCION,
                    nivel = NivelHabitoQuri.POSITIVO,
                    metrica = FechaQuri.mostrar(fechaEstimada.toString()),
                    explicacion = "Quri estima la fecha de llegada con tu margen mensual disponible."
                )
            }
        }.sortedWith(compareBy<InsightFinancieroQuri> { prioridadNivel(it.nivel) }).take(3)
    }

    private fun estimarAportacionMensual(
        fondo: MetaEntidad,
        fondos: List<MetaEntidad>,
        capacidadAhorroMes: Long,
        hoy: LocalDate
    ): Long {
        val pendientes = fondos.filter { it.cantidadObjetivoCentimos > it.cantidadActualCentimos }
        if (pendientes.isEmpty()) return 0L
        val pesos = pendientes.associateWith { pendiente ->
            val urgencia = runCatching { ChronoUnit.MONTHS.between(hoy.withDayOfMonth(1), FechaQuri.parsear(pendiente.fechaLimite).withDayOfMonth(1)).coerceAtLeast(1L) }.getOrDefault(12L)
            val prioridad = (4 - pendiente.prioridad.coerceIn(1, 3)).toLong()
            prioridad * 12L / urgencia
        }
        val totalPeso = pesos.values.sum().coerceAtLeast(1L)
        val pesoFondo = pesos[fondo] ?: return 0L
        return (capacidadAhorroMes * pesoFondo / totalPeso).coerceAtLeast(0L)
    }

    private fun detectarGastosRepetidos(
        gastos: List<GastoEntidad>,
        mesActual: YearMonth
    ): List<InsightFinancieroQuri> {
        val mesesRecientes = setOf(mesActual, mesActual.minusMonths(1), mesActual.minusMonths(2))
        return gastos
            .mapNotNull { gasto -> perteneceAUnMes(gasto.fecha, mesesRecientes)?.let { mes -> gasto to mes } }
            .groupBy { it.first.categoria.trim().lowercase() to redondearAproximado(it.first.cantidadCentimos) }
            .mapNotNull { (clave, registros) ->
                val meses = registros.map { it.second }.distinct()
                if (meses.size < 3) return@mapNotNull null
                val categoria = registros.first().first.categoria.trim().ifBlank { "gasto" }
                val media = registros.map { it.first.cantidadCentimos }.average().toLong()
                InsightFinancieroQuri(
                    titulo = "Gasto repetido: $categoria",
                    detalle = "Aparece en los ultimos ${meses.size} meses con importes parecidos. Puede ser suscripcion o gasto fijo.",
                    tipo = TipoInsightQuri.GASTO_REPETIDO,
                    nivel = if (registros.any { it.first.etiqueta.equals("Innecesario", ignoreCase = true) }) NivelHabitoQuri.AVISO else NivelHabitoQuri.POSITIVO,
                    metrica = formatearDinero(media),
                    explicacion = "Quri busca la misma categoria con cantidades similares durante varios meses."
                )
            }
            .take(3)
    }

    private fun perteneceAUnMes(fecha: String, meses: Set<YearMonth>): YearMonth? =
        runCatching { YearMonth.from(FechaQuri.parsear(fecha)) }.getOrNull()?.takeIf { it in meses }

    private fun redondearAproximado(centimos: Long): Long =
        ((centimos + 10_00 / 2) / 10_00) * 10_00

    private fun mesesHasta(hoy: LocalDate, limite: LocalDate): Long =
        ChronoUnit.MONTHS.between(hoy.withDayOfMonth(1), limite.withDayOfMonth(1)).coerceAtLeast(1L)

    private fun categoriaConMayorSubida(
        gastosMes: List<GastoEntidad>,
        gastosMesAnterior: List<GastoEntidad>
    ): Triple<String, Long, Double>? {
        val actual = gastosMes.groupBy { it.categoria.trim().ifBlank { "Otros" } }.mapValues { it.value.sumOf { gasto -> gasto.cantidadCentimos } }
        val anterior = gastosMesAnterior.groupBy { it.categoria.trim().ifBlank { "Otros" } }.mapValues { it.value.sumOf { gasto -> gasto.cantidadCentimos } }

        return actual.mapNotNull { (categoria, totalActual) ->
            val totalAnterior = anterior[categoria] ?: 0L
            val subida = totalActual - totalAnterior
            if (subida < 30_00) return@mapNotNull null
            val porcentaje = if (totalAnterior > 0L) subida.toDouble() / totalAnterior.toDouble() * 100.0 else 100.0
            if (porcentaje < 20.0) return@mapNotNull null
            Triple(categoria, subida, porcentaje)
        }.maxByOrNull { it.second }
    }

    private fun calcularSaludFinanciera(
        ingresosMes: Long,
        totalGastosMes: Long,
        capacidadAhorroMes: Long,
        fondos: List<MetaEntidad>,
        gastosMes: List<GastoEntidad>,
        hoy: LocalDate
    ): Int {
        if (ingresosMes <= 0L) return 40
        var salud = 70
        val ratioGasto = totalGastosMes.toDouble() / ingresosMes.toDouble()
        val necesarioFondos = fondos.sumOf { necesarioMensual(it, hoy) }
        val innecesario = gastosMes.filter { it.etiqueta.equals("Innecesario", ignoreCase = true) }.sumOf { it.cantidadCentimos }
        val ratioInnecesario = if (totalGastosMes > 0L) innecesario.toDouble() / totalGastosMes.toDouble() else 0.0
        val fondosEnRiesgo = fondos.count { fondoEnRiesgo(it, hoy) }

        salud += when {
            ratioGasto < 0.55 -> 15
            ratioGasto < 0.75 -> 5
            ratioGasto <= 1.0 -> -12
            else -> -30
        }
        salud += when {
            capacidadAhorroMes >= necesarioFondos && necesarioFondos > 0L -> 10
            capacidadAhorroMes > 0L -> 2
            else -> -18
        }
        salud -= (ratioInnecesario * 25).toInt()
        salud -= fondosEnRiesgo * 8
        return salud.coerceIn(0, 100)
    }

    private fun resumenInteligente(
        salud: Int,
        capacidadAhorroMes: Long,
        insights: List<InsightFinancieroQuri>
    ): String {
        val principal = insights.firstOrNull()
        return when {
            principal?.nivel == NivelHabitoQuri.RIESGO -> "Atencion: ${principal.detalle}"
            salud >= 80 -> "Vas fuerte: tienes margen de ${formatearDinero(capacidadAhorroMes.coerceAtLeast(0L))} y tus metas pueden avanzar mas rapido."
            salud >= 60 -> "Buen ritmo: revisa los avisos y mantendras tus fondos dentro del plan."
            salud >= 40 -> "Hay margen de mejora: Quri detecta presion en gastos o fondos."
            else -> "Situacion delicada: prioriza gastos necesarios y protege los fondos importantes."
        }
    }

    private fun recomendacionGastoInnecesario(gastosMes: List<GastoEntidad>): RecomendacionHabitoQuri? {
        val innecesario = gastosMes
            .filter { it.etiqueta.equals("Innecesario", ignoreCase = true) }
            .sumOf { it.cantidadCentimos }
        val total = gastosMes.sumOf { it.cantidadCentimos }
        if (total <= 0L || innecesario <= 0L) return null

        val porcentaje = (innecesario * 100 / total).toInt()
        return if (porcentaje >= 25) {
            RecomendacionHabitoQuri(
                titulo = "Recorta gasto innecesario",
                detalle = "El $porcentaje% de tus gastos del mes es innecesario. Reducirlo puede acelerar tus fondos.",
                nivel = NivelHabitoQuri.AVISO
            )
        } else {
            null
        }
    }

    private fun recomendacionesFondos(fondos: List<MetaEntidad>, hoy: LocalDate): List<RecomendacionHabitoQuri> =
        fondos.mapNotNull { fondo ->
            val objetivo = fondo.cantidadObjetivoCentimos
            if (objetivo <= 0L) return@mapNotNull null

            val restante = (objetivo - fondo.cantidadActualCentimos).coerceAtLeast(0L)
            val progreso = fondo.cantidadActualCentimos.toDouble() / objetivo.toDouble()
            val limite = runCatching { FechaQuri.parsear(fondo.fechaLimite) }.getOrNull()

            when {
                restante == 0L -> RecomendacionHabitoQuri(
                    titulo = "${fondo.nombre} completado",
                    detalle = "Este fondo ya llego al objetivo. Puedes redirigir nuevos ingresos a otra meta.",
                    nivel = NivelHabitoQuri.POSITIVO,
                    fondoId = fondo.id
                )
                limite != null && limite.isBefore(hoy) -> RecomendacionHabitoQuri(
                    titulo = "${fondo.nombre} va tarde",
                    detalle = "La fecha limite ya paso y aun faltan ${formatearDinero(restante)}.",
                    nivel = NivelHabitoQuri.RIESGO,
                    fondoId = fondo.id
                )
                limite != null -> recomendacionPorFecha(fondo, restante, progreso, limite, hoy)
                else -> null
            }
        }

    private fun recomendacionPorFecha(
        fondo: MetaEntidad,
        restante: Long,
        progreso: Double,
        limite: LocalDate,
        hoy: LocalDate
    ): RecomendacionHabitoQuri {
        val diasRestantes = ChronoUnit.DAYS.between(hoy, limite).coerceAtLeast(0L)
        val mesesRestantes = ChronoUnit.MONTHS.between(hoy.withDayOfMonth(1), limite.withDayOfMonth(1)).coerceAtLeast(1L)
        val necesarioMensual = (restante + mesesRestantes - 1L) / mesesRestantes
        val progresoEsperado = progresoTiempo(hoy, limite).toDouble()

        return when {
            diasRestantes <= 30 && restante > 0L -> RecomendacionHabitoQuri(
                titulo = "${fondo.nombre} vence pronto",
                detalle = "Quedan $diasRestantes dias y necesitas ${formatearDinero(restante)} para completarlo.",
                nivel = NivelHabitoQuri.RIESGO,
                fondoId = fondo.id
            )
            progreso + 0.10 < progresoEsperado -> RecomendacionHabitoQuri(
                titulo = "Refuerza ${fondo.nombre}",
                detalle = "Necesitas unos ${formatearDinero(necesarioMensual)}/mes para llegar a tiempo.",
                nivel = NivelHabitoQuri.AVISO,
                fondoId = fondo.id
            )
            progreso >= progresoEsperado -> RecomendacionHabitoQuri(
                titulo = "${fondo.nombre} va bien",
                detalle = "El fondo avanza al ritmo necesario para la fecha limite.",
                nivel = NivelHabitoQuri.POSITIVO,
                fondoId = fondo.id
            )
            else -> RecomendacionHabitoQuri(
                titulo = "Vigila ${fondo.nombre}",
                detalle = "No esta en riesgo grave, pero conviene mantener el reparto mensual.",
                nivel = NivelHabitoQuri.AVISO,
                fondoId = fondo.id
            )
        }
    }

    private fun fondoEnRiesgo(fondo: MetaEntidad, hoy: LocalDate): Boolean {
        val limite = runCatching { FechaQuri.parsear(fondo.fechaLimite) }.getOrNull() ?: return false
        if (fondo.cantidadObjetivoCentimos <= 0L) return false
        val progreso = fondo.cantidadActualCentimos.toFloat() / fondo.cantidadObjetivoCentimos.toFloat()
        return progreso + 0.10f < progresoTiempo(hoy, limite)
    }

    private fun necesarioMensual(fondo: MetaEntidad, hoy: LocalDate): Long {
        val restante = (fondo.cantidadObjetivoCentimos - fondo.cantidadActualCentimos).coerceAtLeast(0L)
        if (restante <= 0L) return 0L
        val limite = runCatching { FechaQuri.parsear(fondo.fechaLimite) }.getOrNull() ?: return 0L
        val meses = ChronoUnit.MONTHS.between(hoy.withDayOfMonth(1), limite.withDayOfMonth(1)).coerceAtLeast(1L)
        return (restante + meses - 1L) / meses
    }

    private fun perteneceAlMes(fecha: String, mes: YearMonth): Boolean =
        runCatching { YearMonth.from(FechaQuri.parsear(fecha)) == mes }.getOrDefault(false)

    private fun progresoTiempo(hoy: LocalDate, limite: LocalDate): Float {
        val referencia = limite.minusYears(1)
        val totalDias = ChronoUnit.DAYS.between(referencia, limite).coerceAtLeast(1L)
        val diasConsumidos = ChronoUnit.DAYS.between(referencia, hoy).coerceIn(0L, totalDias)
        return (diasConsumidos.toFloat() / totalDias.toFloat()).coerceIn(0f, 1f)
    }

    private fun prioridadNivel(nivel: NivelHabitoQuri): Int =
        when (nivel) {
            NivelHabitoQuri.RIESGO -> 0
            NivelHabitoQuri.AVISO -> 1
            NivelHabitoQuri.POSITIVO -> 2
        }

    private fun formatearPorcentaje(valor: Double): String =
        kotlin.math.round(valor).toInt().toString()
}

