@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.romtracker.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Shape

/**
 * Tema central de RomTracker (0.5): claro, sobrio, estilo "Salud" pero nativo Android.
 * Única fuente de colores, formas y tipografía; las pantallas no definen los suyos.
 */
object Paleta {
    val Fondo = Color(0xFFFDF8F0)        // crema cálido
    val Tarjeta = Color(0xFFF6EBDD)      // beige
    val Texto = Color(0xFF1F1B16)        // contraste alto
    val TextoSecundario = Color(0xFF5F5A52)
    val Titulo = Color(0xFF7A5A12)       // dorado oliva de los títulos de tarjeta
    val Tenue = Color(0xFF9A9183)        // ✕ y datos de poca importancia
    val Acento = Color(0xFF4C6B43)       // verde oliva (botones, barras)
    val SobreAcento = Color(0xFFFFFFFF)
    val Pista = Color(0xFFD9D5CC)        // fondo de barras y bordes suaves
    val Borde = Color(0xFFC9BFAE)
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
    surfaceContainerHigh = Paleta.Tarjeta, // fondo de los diálogos
    surfaceTint = Color.Transparent,
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

/** Figtree (variable) incluida en el APK: funciona sin Internet. */
private val Figtree = FontFamily(
    listOf(
        FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold,
    ).map { peso ->
        Font(R.font.figtree, weight = peso, variationSettings = FontVariation.Settings(FontVariation.weight(peso.weight)))
    }
)

private val Tipografia = Typography(
    headlineMedium = TextStyle(fontFamily = Figtree, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = Figtree, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontFamily = Figtree, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = Figtree, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = Figtree, fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Figtree, fontSize = 15.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = Figtree, fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Figtree, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontFamily = Figtree, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontFamily = Figtree, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun TemaRomTracker(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, shapes = Formas, typography = Tipografia, content = contenido)
}

/** Tarjeta beige de esquinas ~20 dp con sombra suave. [alTocar] null = no clicable. */
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

/** Campo de texto del tema: fondo beige, esquinas redondeadas, borde que pasa a verde al enfocar. */
@Composable
fun CampoRom(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Paleta.Tarjeta,
            unfocusedContainerColor = Paleta.Tarjeta,
            focusedBorderColor = Paleta.Acento,
            unfocusedBorderColor = Paleta.Borde,
            focusedLabelColor = Paleta.Acento,
            unfocusedLabelColor = Paleta.TextoSecundario,
            cursorColor = Paleta.Acento,
        ),
    )
}

/** Botón grande y redondeado (alto ≥ 56 dp). [destacado] = relleno verde; si no, contorno suave. */
@Composable
fun BotonRom(
    texto: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = true,
    habilitado: Boolean = true,
    alTocar: () -> Unit,
) {
    val m = modifier.heightIn(min = 56.dp)
    val forma = MaterialTheme.shapes.medium
    if (destacado) {
        Button(
            onClick = alTocar, enabled = habilitado, modifier = m, shape = forma,
            colors = ButtonDefaults.buttonColors(containerColor = Paleta.Acento, contentColor = Paleta.SobreAcento),
        ) { Text(texto, style = MaterialTheme.typography.titleSmall) }
    } else {
        OutlinedButton(
            onClick = alTocar, enabled = habilitado, modifier = m, shape = forma,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
            border = BorderStroke(1.dp, Paleta.Borde),
        ) { Text(texto, style = MaterialTheme.typography.titleSmall) }
    }
}

/** Selector de hora dentro de una tarjeta, con la esfera en gris claro. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelojRom(estado: TimePickerState) {
    TarjetaRom(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            TimePicker(
                state = estado,
                colors = TimePickerDefaults.colors(clockDialColor = Paleta.Fondo),
            )
        }
    }
}
