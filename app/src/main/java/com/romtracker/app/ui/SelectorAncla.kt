package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.logic.anclaDeHabito

/**
 * R16: atajo para apilar un hábito sobre otro. Tocar un hábito completa la clave ancla con
 * "mi hábito «X»"; el texto se puede seguir editando. No se muestra si no hay otros hábitos.
 */
@Composable
fun SelectorAncla(nombres: List<String>, alElegir: (String) -> Unit) {
    if (nombres.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("¿O después de otro hábito tuyo?", style = MaterialTheme.typography.titleMedium)
        nombres.forEach { n ->
            BotonRom(
                texto = "Después de «$n»",
                modifier = Modifier.fillMaxWidth(),
                alTocar = { alElegir(anclaDeHabito(n)) },
            )
        }
    }
}
