package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Factory crea instancias de InicioViewModel
 * pasando el repositorio al constructor.
 */
class InicioViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InicioViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}