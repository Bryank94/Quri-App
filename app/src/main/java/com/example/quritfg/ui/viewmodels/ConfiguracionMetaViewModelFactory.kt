package com.example.quritfg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quritfg.datos.analytics.AnalyticsTracker
import com.example.quritfg.datos.repositorio.RepositorioQuriRoom

/**
 * Factory que crea instancias de ConfiguracionMetaViewModel
 * permitiendo pasar el repositorio al constructor.
 */
class ConfiguracionMetaViewModelFactory(
    private val repositorio: RepositorioQuriRoom,
    private val analytics: AnalyticsTracker? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracionMetaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfiguracionMetaViewModel(repositorio, analytics) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
