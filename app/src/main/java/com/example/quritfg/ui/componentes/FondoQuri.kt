package com.example.quritfg.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.quritfg.R
import com.example.quritfg.ui.config.LocalQuriSettings

@Composable
fun FondoQuri(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.quri_premium_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (temaOscuro) {
                            listOf(
                                Color(0xD8010805),
                                Color(0x66052314),
                                Color(0xCC010805)
                            )
                        } else {
                            listOf(
                                Color(0x77F2F7EF),
                                Color(0x44FFF4D6),
                                Color(0xAA0B2B18)
                            )
                        },
                        start = Offset.Zero,
                        end = Offset(1200f, 2200f)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x11FFFFFF),
                            Color.Transparent
                        ),
                        center = Offset(900f, 320f),
                        radius = 980f
                    )
                )
        )

        content()
    }
}
