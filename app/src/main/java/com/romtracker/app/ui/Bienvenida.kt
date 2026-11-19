package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Perfil

/**
 * Pantalla P1: 3 pasos. Solo llama a [alTerminar] al final con el perfil completo;
 * si se cierra a mitad de camino no se guarda nada (la app vuelve al paso 1).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bienvenida(alTerminar: (Perfil) -> Unit) {
    var paso by rememberSaveable { mutableIntStateOf(1) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableIntStateOf(18) }
    var minuto by rememberSaveable { mutableIntStateOf(0) }
    var ancla by rememberSaveable { mutableStateOf("") }
    var porQue by rememberSaveable { mutableStateOf("") }
    val relojState = rememberTimePickerState(initialHour = hora, initialMinute = minuto, is24Hour = true)

    val puedeSeguir = when (paso) {
        1 -> nombre.isNotBlank()
        2 -> ancla.isNotBlank()
        else -> porQue.isNotBlank()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Paso $paso de 3", style = MaterialTheme.typography.labelLarge)

        when (paso) {
            1 -> {
                Text("¿Cómo te llamás?", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                )
                Spacer(Modifier.height(8.dp))
                Text("¿A qué hora salís del trabajo?", style = MaterialTheme.typography.headlineSmall)
                TimePicker(state = relojState)
            }
            2 -> {
                Text("Tu clave ancla", style = MaterialTheme.typography.headlineSmall)
                Text("Un hábito se apoya en algo que ya hacés todos los días.")
                OutlinedTextField(
                    value = ancla,
                    onValueChange = { ancla = it },
                    label = { Text("Después de…") },
                    placeholder = { Text("salir del trabajo") },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (ancla.isNotBlank()) Text("Después de ${ancla.trim()}, voy al gym.")
                OutlinedButton(onClick = { ancla = "salir del trabajo" }) {
                    Text("Usar: salir del trabajo")
                }
            }
            else -> {
                Text("¿Por qué es importante para vos?", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = porQue,
                    onValueChange = { porQue = it },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (paso > 1) {
                OutlinedButton(onClick = { paso -= 1 }) { Text("Atrás") }
            }
            Button(
                enabled = puedeSeguir,
                onClick = {
                    if (paso == 1) {
                        hora = relojState.hour
                        minuto = relojState.minute
                    }
                    if (paso < 3) {
                        paso += 1
                    } else {
                        alTerminar(
                            Perfil(
                                nombre = nombre.trim(),
                                horaSalidaMinutos = hora * 60 + minuto,
                                claveAncla = ancla.trim(),
                                porQue = porQue.trim(),
                            )
                        )
                    }
                },
            ) { Text(if (paso < 3) "Siguiente" else "Empezar") }
        }
    }
}
