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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.TemaPerfil

/** Pantalla de Ajustes: nombre, hora de salida, tema y tarjeta de agua. No toca registros, fase ni consistencia. */
@Composable
fun Ajustes(perfil: Perfil, alVolver: () -> Unit, alGuardar: (Perfil) -> Unit, alCambiarTema: (Int) -> Unit) {
    BackHandler(onBack = alVolver)

    var nombre by rememberSaveable { mutableStateOf(perfil.nombre) }
    var aguaActiva by rememberSaveable { mutableStateOf(perfil.aguaActiva) }
    var metaAgua by rememberSaveable { mutableStateOf(perfil.metaAgua.toString()) }
    var horaSalida by rememberSaveable { mutableStateOf(perfil.horaSalidaMinutos) }
    val metaValida = metaAgua.toIntOrNull()?.let { it in 1..30 } == true
    val valido = nombre.isNotBlank() && (!aguaActiva || metaValida)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Ajustes", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto) }

        Text("Tema", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(TemaPerfil.CLARO to "Claro", TemaPerfil.OSCURO to "Oscuro")
                .forEach { (valor, texto) ->
                    BotonRom(
                        texto = texto,
                        modifier = Modifier.weight(1f),
                        destacado = TemaPerfil.esOscuro(perfil.tema) == TemaPerfil.esOscuro(valor),
                        alTocar = { alCambiarTema(valor) },
                    )
                }
        }

        CampoRom(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )

        Text("Hora de salida del trabajo", style = MaterialTheme.typography.titleMedium)
        HoraSalidaRom(horaSalida) { horaSalida = it }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Mostrar la tarjeta de agua", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Switch(checked = aguaActiva, onCheckedChange = { aguaActiva = it })
        }
        if (!aguaActiva) Text("Si la ocultás, tu historial de agua se conserva.", color = Paleta.TextoSecundario)

        if (aguaActiva) CampoRom(
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
                    perfil.copy(
                        nombre = nombre.trim(),
                        horaSalidaMinutos = horaSalida,
                        aguaActiva = aguaActiva,
                        metaAgua = metaAgua.toIntOrNull()?.takeIf { it in 1..30 } ?: perfil.metaAgua,
                    )
                )
            },
        )
    }
}
