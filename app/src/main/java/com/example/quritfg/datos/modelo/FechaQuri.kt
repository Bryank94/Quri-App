package com.example.quritfg.datos.modelo

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object FechaQuri {
    const val FORMATO_USUARIO = "dd-MM-yyyy"

    private val formatoUsuario = DateTimeFormatter.ofPattern(FORMATO_USUARIO)
    private val formatoIso = DateTimeFormatter.ISO_LOCAL_DATE

    fun hoyTexto(): String =
        LocalDate.now().format(formatoUsuario)

    fun parsear(fecha: String): LocalDate =
        runCatching { LocalDate.parse(fecha.trim(), formatoUsuario) }
            .getOrElse { LocalDate.parse(fecha.trim(), formatoIso) }

    fun mostrar(fecha: String): String =
        runCatching { parsear(fecha).format(formatoUsuario) }.getOrDefault(fecha)
}
