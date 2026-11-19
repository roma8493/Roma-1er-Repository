package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Perfil

/** Pantalla de Ajustes: edita los 4 datos de la Bienvenida. No toca registros, fase ni consistencia. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ajustes(perfil: Perfil, alVolver: () -> Unit, alGuardar: (Perfil) -> Unit) {
    BackHandler(onBack = alVolver)

    var nombre by rememberSaveable { mutableStateOf(perfil.nombre) }
    var ancla by rememberSaveable { mutableStateOf(perfil.claveAncla) }
    var porQue by rememberSaveable { mutableStateOf(perfil.porQue) }
    val reloj = rememberTimePickerState(
        initialHour = perfil.horaSalidaMinutos / 60,
        initialMinute = perfil.horaSalidaMinutos % 60,
        is24Hour = true,
    )
    val valido = nombre.isNotBlank() && ancla.isNotBlank() && porQue.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Ajustes", style = MaterialTheme.typography.titleLarge) }

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )

        Text("Hora de salida del trabajo", style = MaterialTheme.typography.titleMedium)
        TimePicker(state = reloj)

        OutlinedTextField(
            value = ancla,
            onValueChange = { ancla = it },
            label = { Text("Clave ancla: Después de…") },
            modifier = Modifier.fillMaxWidth(),
        )
        if (ancla.isNotBlank()) Text("Después de ${ancla.trim()}, voy al gym.")

        OutlinedTextField(
            value = porQue,
            onValueChange = { porQue = it },
            label = { Text("¿Por qué es importante para vos?") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            enabled = valido,
            onClick = {
                alGuardar(
                    Perfil(
                        nombre = nombre.trim(),
                        horaSalidaMinutos = reloj.hour * 60 + reloj.minute,
                        claveAncla = ancla.trim(),
                        porQue = porQue.trim(),
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        ) { Text("Guardar cambios") }
    }
}
