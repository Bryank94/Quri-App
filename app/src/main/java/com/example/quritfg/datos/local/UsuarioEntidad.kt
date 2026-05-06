package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Modelo de usuario en la base de datos.
 *
 * Se usa para guardar los datos necesarios para login.
 * No tiene mucha complicacion, es bastante directo.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntidad(

    // id autogenerado, clave primaria
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // email del usuario
    val email: String,

    val passwordHash: String,

    val passwordSalt: String,

    val authVersion: Int = 1,

    val passwordIterations: Int = 1
)
