package com.example.quritfg.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntidad(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val email: String,
    val password: String
)