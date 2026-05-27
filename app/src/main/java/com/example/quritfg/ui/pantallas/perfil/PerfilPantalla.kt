package com.example.quritfg.ui.pantallas.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quritfg.R
import com.example.quritfg.ui.componentes.EncabezadoTarjetaQuri
import com.example.quritfg.ui.componentes.TarjetaQuri
import com.example.quritfg.ui.componentes.TituloPantallaQuri
import com.example.quritfg.ui.config.quriTexto
import com.example.quritfg.ui.theme.DoradoDinero

@Composable
fun PerfilPantalla(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantallaQuri(quriTexto("Perfil", "Profile"))
        TarjetaQuri {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.quri_profile),
                    contentDescription = "Perfil Quri",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
                Text("Quri", color = DoradoDinero, fontWeight = FontWeight.Bold)
                Text(quriTexto("Tu espacio de ajustes y progreso personal.", "Your settings and personal progress space."), color = Color.White)
            }
        }
        TarjetaQuri {
            EncabezadoTarjetaQuri("SET", quriTexto("Ajustes", "Settings"))
            Text(quriTexto("Idioma, moneda, seguridad y preferencias siguen disponibles en el boton superior de perfil.", "Language, currency, security and preferences are still available in the top profile button."), color = Color.White)
        }
    }
}
