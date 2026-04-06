package com.example.quritfg.datos.di

import android.content.Context
import com.example.quritfg.datos.local.BaseDatosQuri
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

object ModuloApp {

    @Volatile
    private var repositorio: RepositorioQuriRoom? = null

    fun proporcionarRepositorio(context: Context): RepositorioQuriRoom {
        return repositorio ?: synchronized(this) {

            val baseDatos =
                BaseDatosQuri.obtenerBaseDatos(context.applicationContext)

            val nuevoRepositorio = RepositorioQuriRoom(
                baseDatos.metaDao(),
                baseDatos.gastoDao(),
                baseDatos.ingresoDao(),
                baseDatos.usuarioDao() // 🔥 NUEVO (CLAVE)
            )

            repositorio = nuevoRepositorio
            nuevoRepositorio
        }
    }
}