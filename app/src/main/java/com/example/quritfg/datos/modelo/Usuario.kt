package com.example.quritfg.datos.modelo

/**
 * Representa los datos basicos del usuario dentro de la aplicacion.
 *
 * Se utiliza para almacenar la informacion introducida
 * en la pantalla de registro.
 */
data class Usuario(
    val correo: String,
    val contrasena: String
)