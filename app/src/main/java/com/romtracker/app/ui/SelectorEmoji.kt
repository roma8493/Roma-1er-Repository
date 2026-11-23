package com.romtracker.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.logic.EMOJIS_SUGERIDOS
import com.romtracker.app.logic.primerEmoji

/**
 * Selector de emoji del hábito: cuadrícula de sugeridos + campo para escribir otro.
 * [valor] vacío = se usa el de por defecto según el tipo ([porDefecto], solo se muestra como aviso).
 */
@Composable
fun SelectorEmoji(valor: String, porDefecto: String, alCambiar: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Emoji del hábito", style = MaterialTheme.typography.titleMedium)
        Text(
            if (valor.isEmpty()) "Sin elegir: se usa $porDefecto. Tocá uno para cambiarlo."
            else "Elegido: $valor. Tocalo de nuevo para volver al de por defecto.",
            style = MaterialTheme.typography.bodySmall,
            color = Paleta.TextoSecundario,
        )
        EMOJIS_SUGERIDOS.chunked(6).forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                fila.forEach { e ->
                    val elegido = e == valor
                    Box(
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .padding(2.dp)
                            .border(
                                BorderStroke(if (elegido) 2.dp else 1.dp, if (elegido) Paleta.Acento else Paleta.Borde),
                                MaterialTheme.shapes.small,
                            )
                            .clickable { alCambiar(if (elegido) "" else e) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(e, style = MaterialTheme.typography.titleLarge) }
                }
            }
        }
        CampoRom(
            value = if (valor in EMOJIS_SUGERIDOS) "" else valor,
            onValueChange = { alCambiar(primerEmoji(it)) },
            label = { Text("Otro emoji (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
