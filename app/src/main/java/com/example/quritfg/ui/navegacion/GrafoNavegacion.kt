package com.example.quritfg.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.quritfg.ui.pantallas.anadir.AnadirGastoPantalla
import com.example.quritfg.ui.pantallas.admin.AdminPantalla
import com.example.quritfg.ui.pantallas.banco.SimulacionBancariaPantalla
import com.example.quritfg.ui.pantallas.configuracion.ConfiguracionMetaPantalla
import com.example.quritfg.ui.pantallas.finanzas.FinanzasPantalla
import com.example.quritfg.ui.pantallas.historial.HistorialPantalla
import com.example.quritfg.ui.pantallas.inicio.InicioPantalla
import com.example.quritfg.ui.pantallas.metas.MetasPantalla
import com.example.quritfg.ui.pantallas.perfil.PerfilPantalla
import com.example.quritfg.ui.pantallas.onboarding.OnboardingPantalla
import com.example.quritfg.ui.pantallas.plan.PlanMensualPantalla
import com.example.quritfg.ui.pantallas.progreso.ProgresoPantalla
import com.example.quritfg.ui.pantallas.registro.RegistroPantalla
import com.example.quritfg.ui.pantallas.login.LoginPantalla

/**
 * Define todas las pantallas de la aplicacion y
 * como se navega entre ellas.
 */
@Composable
fun GrafoNavegacion(
    navController: NavHostController,
    startDestination: String, // 🔥 AÑADIDO
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = startDestination, // 🔥 CAMBIADO
        modifier = modifier
    ) {

        // REGISTRO
        composable(Rutas.Registro.ruta) {
            RegistroPantalla(navController)
        }

        // LOGIN
        composable(Rutas.Login.ruta) {
            LoginPantalla(navController)
        }

        composable(Rutas.Admin.ruta) {
            AdminPantalla(navController)
        }

        composable(Rutas.Onboarding.ruta) {
            OnboardingPantalla(navController)
        }

        //  CONFIGURACIÓN
        composable(Rutas.ConfiguracionMeta.ruta) {
            ConfiguracionMetaPantalla(
                navController,
                esPrimeraConfiguracion = true
            )
        }

        // INICIO
        composable(Rutas.Inicio.ruta) {
            InicioPantalla(navController)
        }

        // AÑADIR GASTO
        composable(Rutas.AnadirGasto.ruta) {
            AnadirGastoPantalla(navController)
        }

        // PROGRESO
        composable(Rutas.Progreso.ruta) {
            ProgresoPantalla(navController)
        }

        // HISTORIAL
        composable(Rutas.Historial.ruta) {
            HistorialPantalla(navController)
        }

        // METAS
        composable(Rutas.Metas.ruta) {
            MetasPantalla(navController)
        }

        composable(Rutas.Banco.ruta) {
            SimulacionBancariaPantalla(navController)
        }

        composable(Rutas.PlanMensual.ruta) {
            PlanMensualPantalla(navController)
        }

        composable(Rutas.Finanzas.ruta) {
            FinanzasPantalla(navController)
        }

        composable(Rutas.Perfil.ruta) {
            PerfilPantalla(navController)
        }
    }
}
