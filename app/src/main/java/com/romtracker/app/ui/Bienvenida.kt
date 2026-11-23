package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

/**
 * Pantalla P1: un solo paso (nombre y hora de salida). La clave ancla y el "por qué" se piden por hábito
 * al crearlo. Solo llama a [alTerminar] al final; si se cierra antes no se guarda nada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bienvenida(alTerminar: (Perfil) -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    val relojState = rememberTimePickerState(initialHour = 18, initialMinute = 0, is24Hour = true)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("¿Cómo te llamás?", style = MaterialTheme.typography.headlineMedium)
        CampoRom(
            value = nombre,
            onValueChange = { nombre = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )
        Spacer(Modifier.height(8.dp))
        Text("¿A qué hora salís del trabajo?", style = MaterialTheme.typography.headlineMedium)
        RelojRom(relojState)

        BotonRom(
            texto = "Empezar",
            modifier = Modifier.fillMaxWidth(),
            habilitado = nombre.isNotBlank(),
            alTocar = {
                alTerminar(
                    Perfil(
                        nombre = nombre.trim(),
                        horaSalidaMinutos = relojState.hour * 60 + relojState.minute,
                        claveAncla = "",
                        porQue = "",
                    )
                )
            },
        )
    }
}
