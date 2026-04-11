package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Factory para crear el ViewModel de autenticacion.
 *
 * Se usa cuando el ViewModel necesita parametros (en este caso el repositorio).
 */
class AutenticacionViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // crea el ViewModel pasandole el repositorio
        return AutentificacionViewModel(repositorio) as T
    }
}