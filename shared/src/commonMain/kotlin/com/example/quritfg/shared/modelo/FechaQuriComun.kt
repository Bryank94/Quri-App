package com.example.quritfg.shared.modelo

data class FechaQuriComun(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<FechaQuriComun> {
    override fun compareTo(other: FechaQuriComun): Int =
        compareValuesBy(this, other, FechaQuriComun::year, FechaQuriComun::month, FechaQuriComun::day)

    fun mesesHasta(limite: FechaQuriComun): Long =
        ((limite.year - year) * 12L + (limite.month - month)).coerceAtLeast(1L)

    fun mostrarUsuario(): String =
        "${day.dosDigitos()}-${month.dosDigitos()}-$year"

    private fun Int.dosDigitos(): String =
        if (this < 10) "0$this" else toString()
}

object FechaQuriComunParser {
    fun parsear(fecha: String): FechaQuriComun? {
        val limpia = fecha.trim()
        return parsearUsuario(limpia) ?: parsearIso(limpia)
    }

    private fun parsearUsuario(fecha: String): FechaQuriComun? {
        val partes = fecha.split("-")
        if (partes.size != 3) return null
        val dia = partes[0].toIntOrNull() ?: return null
        val mes = partes[1].toIntOrNull() ?: return null
        val year = partes[2].toIntOrNull() ?: return null
        return crear(year, mes, dia)
    }

    private fun parsearIso(fecha: String): FechaQuriComun? {
        val partes = fecha.split("-")
        if (partes.size != 3) return null
        val year = partes[0].toIntOrNull() ?: return null
        val mes = partes[1].toIntOrNull() ?: return null
        val dia = partes[2].toIntOrNull() ?: return null
        return crear(year, mes, dia)
    }

    private fun crear(year: Int, month: Int, day: Int): FechaQuriComun? {
        if (year !in 1900..2200) return null
        if (month !in 1..12) return null
        if (day !in 1..31) return null
        return FechaQuriComun(year, month, day)
    }
}
