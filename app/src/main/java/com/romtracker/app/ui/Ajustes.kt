package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
    var metaAgua by rememberSaveable { mutableStateOf(perfil.metaAgua.toString()) }
    val reloj = rememberTimePickerState(
        initialHour = perfil.horaSalidaMinutos / 60,
        initialMinute = perfil.horaSalidaMinutos % 60,
        is24Hour = true,
    )
    val metaValida = metaAgua.toIntOrNull()?.let { it in 1..30 } == true
    val valido = nombre.isNotBlank() && ancla.isNotBlank() && porQue.isNotBlank() && metaValida

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Ajustes", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto) }

        CampoRom(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )

        Text("Hora de salida del trabajo", style = MaterialTheme.typography.titleMedium)
        RelojRom(reloj)

        CampoRom(
            value = ancla,
            onValueChange = { ancla = it },
            label = { Text("Clave ancla: Después de…") },
            modifier = Modifier.fillMaxWidth(),
        )
        if (ancla.isNotBlank()) Text("Después de ${ancla.trim()}, voy al gym.", color = Paleta.TextoSecundario)

        CampoRom(
            value = porQue,
            onValueChange = { porQue = it },
            label = { Text("¿Por qué es importante para vos?") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        CampoRom(
            value = metaAgua,
            onValueChange = { metaAgua = it.filter(Char::isDigit).take(2) },
            label = { Text("Meta de vasos de agua por día (1 a 30)") },
            supportingText = { Text("La nueva meta rige desde hoy; los días anteriores conservan la suya.") },
            singleLine = true,
            isError = !metaValida,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        BotonRom(
            texto = "Guardar cambios",
            modifier = Modifier.fillMaxWidth(),
            habilitado = valido,
            alTocar = {
                alGuardar(
                    Perfil(
                        nombre = nombre.trim(),
                        horaSalidaMinutos = reloj.hour * 60 + reloj.minute,
                        claveAncla = ancla.trim(),
                        porQue = porQue.trim(),
                        metaAgua = metaAgua.toInt(),
                    )
                )
            },
        )
    }
}
