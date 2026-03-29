package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Factory encargada de crear el MetasViewModel
 * pasando el repositorio al constructor.
 */
class MetasViewModelFactory(
    private val repositorio: RepositorioQuriRoom
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MetasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MetasViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}