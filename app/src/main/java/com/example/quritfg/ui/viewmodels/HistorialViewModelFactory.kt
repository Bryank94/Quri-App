package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Factory que permite crear el HistorialViewModel
 * pasando el repositorio como parametro.
 */
class HistorialViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistorialViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}