package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Antojo resistido (R11) de un hábito DEJAR: botón "+1" y corrección "−1" (mínimo 0).
 * Es un logro aparte: no toca el ✓, la racha ni la fase.
 * Con [semana] y [total] (Detalle) muestra la línea completa de conteos; sin ellos (Hoy) solo el de hoy.
 */
@Composable
fun BloqueAntojo(
    hoy: Int,
    alSumar: () -> Unit,
    alRestar: () -> Unit,
    modifier: Modifier = Modifier,
    semana: Int? = null,
    total: Int? = null,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonRom("Resistí un antojo 💪", Modifier.weight(1f), destacado = false, alTocar = alSumar)
            BotonRom("−1", destacado = false, habilitado = hoy > 0, compacto = true, alTocar = alRestar)
        }
        val linea = if (semana != null && total != null) {
            "Antojos resistidos: hoy $hoy · esta semana $semana · en total $total"
        } else {
            "Antojos resistidos hoy: $hoy"
        }
        Text(linea, style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
    }
}
