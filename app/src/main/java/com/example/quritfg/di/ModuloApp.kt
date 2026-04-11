package com.example.quritfg.datos.di

import android.content.Context
import com.example.quritfg.datos.local.BaseDatosQuri
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Modulo simple para proporcionar el repositorio.
 *
 * Basicamente evita crear varias instancias y centraliza
 * el acceso a los datos de la app.
 *
 * Es una especie de "punto unico" para obtener el repositorio.
 */
object ModuloApp {

    // instancia unica del repositorio
    @Volatile
    private var repositorio: RepositorioQuriRoom? = null

    /**
     * Devuelve el repositorio.
     *
     * Si no existe lo crea usando la base de datos,
     * si ya existe lo reutiliza.
     */
    fun proporcionarRepositorio(context: Context): RepositorioQuriRoom {
        return repositorio ?: synchronized(this) {

            // obtiene la base de datos
            val baseDatos =
                BaseDatosQuri.obtenerBaseDatos(context.applicationContext)

            // crea el repositorio con todos los DAOs
            val nuevoRepositorio = RepositorioQuriRoom(
                baseDatos.metaDao(),
                baseDatos.gastoDao(),
                baseDatos.ingresoDao(),
                baseDatos.usuarioDao() // importante porque añade usuarios
            )

            repositorio = nuevoRepositorio
            nuevoRepositorio
        }
    }
}