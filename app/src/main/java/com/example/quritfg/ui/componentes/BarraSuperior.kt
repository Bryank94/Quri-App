package com.example.quritfg.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quritfg.R

@Composable
fun BarraSuperior() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_quri),
            contentDescription = "Logo Quri",
            modifier = Modifier
                .fillMaxWidth(0.4f) // 🔥 escala proporcional
                .height(80.dp)
                .padding(vertical = 4.dp) // 🔥 mejor ajuste visual
        )
    }
}