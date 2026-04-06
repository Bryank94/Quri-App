package com.example.quritfg.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quritfg.R

@Composable
fun BarraSuperior() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp) // 🔥 altura fija tipo app real
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_quri),
                contentDescription = "Logo Quri",
                modifier = Modifier
                    .fillMaxWidth(0.35f) // 🔥 mejor proporción
            )
        }
    }
}