package com.example.quritfg.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quritfg.R
import com.example.quritfg.ui.config.LocalQuriSettings
import com.example.quritfg.ui.theme.DoradoDinero
import com.example.quritfg.ui.theme.VerdeDinero

private val QuriCardShape = RoundedCornerShape(18.dp)

@Composable
fun TituloPantallaQuri(
    text: String,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    Text(
        text = text,
        color = if (temaOscuro) Color.White else Color(0xFF06160D),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun TarjetaQuri(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val panelColor = if (temaOscuro) Color(0x7A04180D) else Color(0xDFF8FFF4)
    val glowColor = if (temaOscuro) Color(0x364D6B2B) else Color(0x44FFF4D6)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = panelColor,
            contentColor = if (temaOscuro) Color.White else Color(0xFF06160D)
        ),
        border = BorderStroke(1.2.dp, DoradoDinero.copy(alpha = 0.78f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = QuriCardShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            glowColor,
                            Color.Transparent,
                            DoradoDinero.copy(alpha = 0.10f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
fun EncabezadoTarjetaQuri(
    icono: String,
    titulo: String
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCircularQuri(icono = icono, size = 48)
        Text(
            text = titulo,
            color = DoradoDinero,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FilaDatoQuri(
    icono: String,
    etiqueta: String,
    valor: String,
    valorColor: Color = Color.White,
    detalle: String? = null
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCircularQuri(icono = icono, size = 42)
        Text(
            text = etiqueta,
            color = textoColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = valor,
                color = valorColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
            detalle?.let {
                Text(
                    text = it,
                    color = textoColor.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun BarraProgresoQuri(
    titulo: String,
    porcentaje: Float,
    valorDerecha: String,
    modifier: Modifier = Modifier
) {
    val progreso = porcentaje.coerceIn(0f, 1f)
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    TarjetaQuri(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = textoColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = valorDerecha,
                color = DoradoDinero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xDD021008))
                .border(1.dp, DoradoDinero.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFFF2A0),
                                DoradoDinero,
                                Color.White.copy(alpha = 0.86f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun BarraProgresoLineaQuri(
    titulo: String,
    porcentaje: Float,
    valorDerecha: String
) {
    val progreso = porcentaje.coerceIn(0f, 1f)
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, color = textoColor, fontWeight = FontWeight.Bold)
            Text(text = valorDerecha, color = DoradoDinero, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xDD021008))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFF2A0), DoradoDinero, Color.White.copy(alpha = 0.86f))
                        )
                    )
            )
        }
    }
}

@Composable
fun ConsejoQuri(
    icono: String,
    titulo: String,
    texto: String
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    TarjetaQuri {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconoCircularQuri(icono = icono, size = 58)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = titulo,
                    color = textoColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = texto,
                    color = textoColor.copy(alpha = 0.88f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ConsejoLineaQuri(
    icono: String,
    titulo: String,
    texto: String
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCircularQuri(icono = icono, size = 50)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = titulo, color = textoColor, fontWeight = FontWeight.Bold)
            Text(text = texto, color = textoColor.copy(alpha = 0.84f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun IconoCircularQuri(
    icono: String,
    size: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0x6604160D))
            .border(1.dp, DoradoDinero.copy(alpha = 0.72f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = recursoIconoQuri(icono)),
            contentDescription = null,
            modifier = Modifier.size((size * 0.54f).dp)
        )
    }
}

private fun recursoIconoQuri(icono: String): Int =
    when (icono) {
        "DOC" -> R.drawable.ic_quri_doc
        "Q" -> R.drawable.ic_quri_coins
        "OBJ" -> R.drawable.ic_quri_target
        "UP" -> R.drawable.ic_quri_trending_up
        "DN" -> R.drawable.ic_quri_trending_down
        "$" -> R.drawable.ic_quri_wallet
        "PIG" -> R.drawable.ic_quri_pig
        "CAL" -> R.drawable.ic_quri_calendar
        "CLK" -> R.drawable.ic_quri_clock
        "SAFE" -> R.drawable.ic_quri_safe
        "*" -> R.drawable.ic_quri_star
        "%" -> R.drawable.ic_quri_percent
        "!" -> R.drawable.ic_quri_alert
        "+" -> R.drawable.ic_quri_wallet
        else -> R.drawable.ic_quri_star
    }

@Composable
fun SeparadorQuri() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DoradoDinero.copy(alpha = 0.18f))
    )
}

@Composable
fun LeyendaColorQuri(
    color: Color,
    texto: String,
    valor: String
) {
    val temaOscuro = LocalQuriSettings.current.temaOscuro
    val textoColor = if (temaOscuro) Color.White else Color(0xFF06160D)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = texto, color = textoColor, modifier = Modifier.weight(1f))
        Text(text = valor, color = color, fontWeight = FontWeight.Bold)
    }
}

fun colorDineroPositivo() = VerdeDinero
fun colorDineroAlerta() = DoradoDinero
fun colorDineroNegativo() = Color(0xFFFF6B3A)
