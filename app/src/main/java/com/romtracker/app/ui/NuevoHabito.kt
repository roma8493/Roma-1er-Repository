package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.TipoPropio

/**
 * Pantalla "Nuevo hábito": nombre, tipo (quiero hacerlo / quiero dejarlo), clave ancla y por qué.
 * El inicio del hábito es la fecha de creación (lo resuelve quien llama a [alCrear]).
 */
@Composable
fun NuevoHabito(
    alVolver: () -> Unit,
    alCrear: (nombre: String, tipo: String, ancla: String, porQue: String, versionMinima: String) -> Unit,
) {
    BackHandler(onBack = alVolver)

    var nombre by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(TipoPropio.HACER) }
    var ancla by rememberSaveable { mutableStateOf("") }
    var porQue by rememberSaveable { mutableStateOf("") }
    var versionMinima by rememberSaveable { mutableStateOf("") }
    val valido = nombre.isNotBlank() && ancla.isNotBlank() && porQue.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Nuevo hábito", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto) }

        CampoRom(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del hábito") },
            placeholder = { Text(if (tipo == TipoPropio.HACER) "Leer 10 minutos" else "Fumar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )

        Text("¿Qué querés con este hábito?", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OpcionTipo("Quiero hacerlo", tipo == TipoPropio.HACER, Modifier.weight(1f)) { tipo = TipoPropio.HACER }
            OpcionTipo("Quiero dejarlo", tipo == TipoPropio.DEJAR, Modifier.weight(1f)) { tipo = TipoPropio.DEJAR }
        }
        Text(
            if (tipo == TipoPropio.HACER) "Cada día marcás si lo hiciste. ✓ = lo hice."
            else "Cada día marcás si lo hiciste. ✓ = ese día no lo hice, y esos días suman a tu racha.",
            style = MaterialTheme.typography.bodySmall,
            color = Paleta.TextoSecundario,
        )

        CampoRom(
            value = ancla,
            onValueChange = { ancla = it },
            label = { Text("Clave ancla: Después de…") },
            placeholder = { Text("tomar el mate de la tarde") },
            modifier = Modifier.fillMaxWidth(),
        )

        CampoRom(
            value = porQue,
            onValueChange = { porQue = it },
            label = { Text("¿Por qué es importante para vos?") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        CampoRom(
            value = versionMinima,
            onValueChange = { versionMinima = it },
            label = { Text("Versión mínima (opcional)") },
            placeholder = { Text("solo 10 minutos") },
            supportingText = { Text("Para los días difíciles: algo chico que igual cuenta.") },
            modifier = Modifier.fillMaxWidth(),
        )

        BotonRom(
            texto = "Crear hábito",
            modifier = Modifier.fillMaxWidth(),
            habilitado = valido,
            alTocar = { alCrear(nombre.trim(), tipo, ancla.trim(), porQue.trim(), versionMinima.trim()) },
        )
    }
}

@Composable
private fun OpcionTipo(texto: String, elegido: Boolean, modifier: Modifier, alTocar: () -> Unit) {
    BotonRom(texto, modifier, destacado = elegido, alTocar = alTocar)
}
