package com.romtracker.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.logic.OpcionesDesencadenante

/**
 * Diálogo opcional (R3) que aparece al registrar un desliz en un hábito DEJAR.
 * Cuatro filas de opciones tocables; volver a tocar la elegida la desmarca (todo es opcional).
 * "Ahora no" y tocar fuera no guardan nada. [alGuardar] recibe null en las filas sin elegir.
 */
@Composable
fun DialogoDesencadenante(
    alGuardar: (lugar: String?, momento: String?, emocion: String?, compania: String?) -> Unit,
    alCerrar: () -> Unit,
) {
    var lugar by rememberSaveable { mutableStateOf<String?>(null) }
    var momento by rememberSaveable { mutableStateOf<String?>(null) }
    var emocion by rememberSaveable { mutableStateOf<String?>(null) }
    var compania by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("¿Querés anotar qué pasó?") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Sirve para encontrar qué cambiar en el entorno.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                FilaOpciones("Lugar", OpcionesDesencadenante.LUGAR, lugar) { lugar = if (lugar == it) null else it }
                FilaOpciones("Momento", OpcionesDesencadenante.MOMENTO, momento) { momento = if (momento == it) null else it }
                FilaOpciones("Emoción", OpcionesDesencadenante.EMOCION, emocion) { emocion = if (emocion == it) null else it }
                FilaOpciones("Compañía", OpcionesDesencadenante.COMPANIA, compania) { compania = if (compania == it) null else it }
            }
        },
        confirmButton = { TextButton(onClick = { alGuardar(lugar, momento, emocion, compania) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Ahora no") } },
    )
}

/** Título + opciones repartidas en filas de hasta 3 (4 → 2+2, 6 → 3+3). */
@Composable
private fun FilaOpciones(titulo: String, opciones: List<String>, elegida: String?, alElegir: (String) -> Unit) {
    val porFila = if (opciones.size <= 3) opciones.size else (opciones.size + 1) / 2
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, color = Paleta.Titulo)
        opciones.chunked(porFila).forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { op -> ChipOpcion(op, op == elegida, Modifier.weight(1f)) { alElegir(op) } }
                // Relleno para que los botones de una fila corta no se estiren más que los demás.
                repeat(porFila - fila.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ChipOpcion(texto: String, elegido: Boolean, modifier: Modifier, alTocar: () -> Unit) {
    val m = modifier.heightIn(min = 48.dp)
    val relleno = PaddingValues(horizontal = 4.dp)
    if (elegido) Button(onClick = alTocar, modifier = m, contentPadding = relleno) { Text(texto, maxLines = 1) }
    else OutlinedButton(
        onClick = alTocar,
        modifier = m,
        contentPadding = relleno,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
        border = BorderStroke(1.dp, Paleta.Borde),
    ) { Text(texto, maxLines = 1) }
}
