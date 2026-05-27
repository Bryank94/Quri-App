package com.example.quritfg

import com.example.quritfg.datos.local.MetaEntidad
import com.example.quritfg.datos.modelo.BancoDemo
import com.example.quritfg.datos.modelo.EstadoFondoBanco
import com.example.quritfg.datos.modelo.ReglasAutomaticasQuri
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BancoDemoTest {
    private val hoy = LocalDate.of(2026, 5, 17)

    @Test
    fun repartePorPrioridadCuandoElDineroEsInsuficiente() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Sofa", objetivo = 300_00, actual = 0, prioridad = 3),
            fondo(id = 2, nombre = "Viaje", objetivo = 900_00, actual = 0, prioridad = 1)
        )

        val plan = BancoDemo.planificarReparto(fondos, ingresoDetectadoCentimos = 100_00, hoy = hoy)

        assertEquals(100_00, plan.repartos.first { it.fondo.nombre == "Viaje" }.asignadoCentimos)
        assertEquals(0L, plan.repartos.first { it.fondo.nombre == "Sofa" }.asignadoCentimos)
        assertEquals(0L, plan.restanteDisponibleCentimos)
    }

    @Test
    fun marcaMetaVencidaComoVasTarde() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Curso", objetivo = 500_00, actual = 100_00, fecha = "01-05-2026")
        )

        val plan = BancoDemo.planificarReparto(fondos, ingresoDetectadoCentimos = 500_00, hoy = hoy)

        assertEquals(EstadoFondoBanco.VAS_TARDE, plan.repartos.first().estado)
    }

    @Test
    fun fondoCompletadoNoRecibeDinero() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Emergencia", objetivo = 100_00, actual = 100_00)
        )

        val plan = BancoDemo.planificarReparto(fondos, ingresoDetectadoCentimos = 500_00, hoy = hoy)

        assertEquals(EstadoFondoBanco.COMPLETADO, plan.repartos.first().estado)
        assertEquals(0L, plan.repartos.first().asignadoCentimos)
        assertEquals(500_00, plan.restanteDisponibleCentimos)
    }

    @Test
    fun dejaSobranteCuandoLaNominaSuperaLoNecesario() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Viaje", objetivo = 600_00, actual = 0, fecha = "17-11-2026")
        )

        val plan = BancoDemo.planificarReparto(fondos, ingresoDetectadoCentimos = 1_300_00, hoy = hoy)

        assertEquals(100_00, plan.repartos.first().asignadoCentimos)
        assertEquals(1_200_00, plan.restanteDisponibleCentimos)
    }

    @Test
    fun calculaSugerenciasMensualesPorFondo() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Viaje", objetivo = 1_200_00, actual = 0, fecha = "17-01-2027"),
            fondo(id = 2, nombre = "Sofa", objetivo = 120_00, actual = 0, fecha = "17-08-2026")
        )

        val plan = BancoDemo.planificarReparto(fondos, ingresoDetectadoCentimos = 1_300_00, hoy = hoy)

        assertEquals(150_00, plan.repartos.first { it.fondo.nombre == "Viaje" }.asignadoCentimos)
        assertEquals(40_00, plan.repartos.first { it.fondo.nombre == "Sofa" }.asignadoCentimos)
    }

    @Test
    fun reglasAutomaticasRespetanMaximoDelIngreso() {
        val fondos = listOf(
            fondo(id = 1, nombre = "Viaje", objetivo = 5_000_00, actual = 0, prioridad = 1),
            fondo(id = 2, nombre = "Emergencia", objetivo = 2_000_00, actual = 0, prioridad = 2)
        )

        val plan = BancoDemo.planificarReparto(
            fondos = fondos,
            ingresoDetectadoCentimos = 1_000_00,
            hoy = hoy,
            reglas = ReglasAutomaticasQuri(
                porcentajesPorNombre = mapOf("viaje" to 15),
                minimosPorNombreCentimos = mapOf("emergencia" to 100_00),
                maximoPorcentajeIngreso = 40,
                priorizarMetasUrgentes = true
            )
        )

        assertEquals(400_00, plan.totalAsignadoCentimos)
        assertEquals(600_00, plan.restanteDisponibleCentimos)
    }

    private fun fondo(
        id: Int,
        nombre: String,
        objetivo: Long,
        actual: Long,
        fecha: String = "17-11-2026",
        prioridad: Int = 2
    ) = MetaEntidad(
        id = id,
        usuarioId = 1,
        nombre = nombre,
        cantidadObjetivoCentimos = objetivo,
        cantidadActualCentimos = actual,
        fechaLimite = fecha,
        prioridad = prioridad
    )
}
