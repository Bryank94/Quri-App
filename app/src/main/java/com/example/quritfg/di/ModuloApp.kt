package com.example.quritfg.datos.di

import android.content.Context
import com.example.quritfg.datos.local.BaseDatosQuri
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Se encarga de proporcionar una unica instancia del repositorio
 * para toda la aplicacion.
 *
 * Evita que se creen multiples repositorios o multiples conexiones
 * a la base de datos.
 */
object ModuloApp {

    @Volatile
    private var repositorio: RepositorioQuriRoom? = null

    /**
     * Devuelve siempre la misma instancia del repositorio.
     * Si no existe, la crea junto con la base de datos.
     */
    fun proporcionarRepositorio(context: Context): RepositorioQuriRoom {
        return repositorio ?: synchronized(this) {

            val baseDatos =
                BaseDatosQuri.obtenerBaseDatos(context.applicationContext)

            val nuevoRepositorio = RepositorioQuriRoom(
                baseDatos.metaDao(),
                baseDatos.gastoDao(),
                baseDatos.ingresoDao()
            )

            repositorio = nuevoRepositorio
            nuevoRepositorio
        }
    }
}