package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de usuario en la base de datos.
 *
 * Se usa para guardar los datos necesarios para login.
 * No tiene mucha complicacion, es bastante directo.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntidad(

    // id autogenerado, clave primaria
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // email del usuario
    val email: String,

    // contraseña (aqui se guarda sin encriptar)
    val password: String
)