package com.romtracker.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Shape

/**
 * Tema central de RomTracker (0.5): claro, sobrio, estilo "Salud" pero nativo Android.
 * Única fuente de colores, formas y tipografía; las pantallas no definen los suyos.
 */
object Paleta {
    val Fondo = Color(0xFFF2F2F7)        // gris muy claro
    val Tarjeta = Color(0xFFFFFFFF)
    val Texto = Color(0xFF1C1C1E)        // contraste alto
    val TextoSecundario = Color(0xFF636366)
    val Acento = Color(0xFF0A60C2)       // único azul sobrio
    val SobreAcento = Color(0xFFFFFFFF)
    val Pista = Color(0xFFE5E5EA)        // fondo de barras y bordes suaves
    val Borde = Color(0xFFC7C7CC)
    val Verde = Color(0xFF1E8A3C)        // ✓
    val Ambar = Color(0xFFB26A00)        // atención
    val Rojo = Color(0xFFC03A2B)         // ✗ suave
}

private val Esquema: ColorScheme = lightColorScheme(
    primary = Paleta.Acento,
    onPrimary = Paleta.SobreAcento,
    secondary = Paleta.Acento,
    onSecondary = Paleta.SobreAcento,
    background = Paleta.Fondo,
    onBackground = Paleta.Texto,
    surface = Paleta.Tarjeta,
    onSurface = Paleta.Texto,
    surfaceVariant = Paleta.Pista,
    onSurfaceVariant = Paleta.TextoSecundario,
    surfaceContainerHighest = Paleta.Tarjeta,
    outline = Paleta.Borde,
    outlineVariant = Paleta.Pista,
    error = Paleta.Rojo,
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private val Tipografia = Typography(
    headlineMedium = TextStyle(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun TemaRomTracker(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, shapes = Formas, typography = Tipografia, content = contenido)
}

/** Tarjeta blanca de esquinas ~20 dp con sombra suave. [alTocar] null = no clicable. */
@Composable
fun TarjetaRom(
    modifier: Modifier = Modifier,
    alTocar: (() -> Unit)? = null,
    habilitada: Boolean = true,
    contenido: @Composable () -> Unit,
) {
    val colores = CardDefaults.cardColors(
        containerColor = Paleta.Tarjeta,
        disabledContainerColor = Paleta.Tarjeta,
    )
    val elevacion = CardDefaults.cardElevation(defaultElevation = 2.dp, disabledElevation = 2.dp)
    val forma = MaterialTheme.shapes.large
    if (alTocar == null) {
        Card(modifier = modifier, shape = forma, colors = colores, elevation = elevacion) { contenido() }
    } else {
        Card(
            onClick = alTocar,
            enabled = habilitada,
            modifier = modifier,
            shape = forma,
            colors = colores,
            elevation = elevacion,
        ) { contenido() }
    }
}

/** Barra de progreso fina y redondeada. [avance] entre 0 y 1. */
@Composable
fun BarraRom(avance: Float, modifier: Modifier = Modifier) {
    val forma: Shape = RoundedCornerShape(50)
    Box(modifier.fillMaxWidth().height(8.dp).clip(forma).background(Paleta.Pista)) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(avance.coerceIn(0f, 1f))
                .clip(forma)
                .background(Paleta.Acento)
        )
    }
}
