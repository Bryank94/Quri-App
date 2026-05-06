package com.example.quritfg.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quritfg.R

/**
 * Barra superior de la app.
 *
 * Solo muestra el logo centrado, estilo simple tipo app real.
 * No tiene acciones ni botones.
 */
@Composable
fun BarraSuperior(
    onLogoClick: (() -> Unit)? = null
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp) // altura fija para que siempre se vea igual
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // centra el contenido
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_quri),
                contentDescription = "Logo Quri",

                // controla el tamaño del logo dentro de la barra
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .then(
                        if (onLogoClick != null) {
                            Modifier.clickable(onClick = onLogoClick)
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}
