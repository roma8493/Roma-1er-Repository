package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Revision
import com.romtracker.app.logic.lunesDe
import com.romtracker.app.logic.revisionesRecientes
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Clave con la que "Después" recuerda la invitación a la revisión (comparte la lista `pospuestos` del check). */
const val CLAVE_REVISION = "REVISION"

/** Tarjeta de Hoy que invita a la revisión semanal (R9). Es una invitación, nunca una obligación (R8). */
@Composable
fun TarjetaInvitacionRevision(alHacer: () -> Unit, alPosponer: () -> Unit) {
    TarjetaRom(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("¿Hacemos una revisión corta de la semana?", style = MaterialTheme.typography.titleMedium)
            Text(
                "Dos preguntas para pensar qué te frenó y qué podés cambiar. Podés dejarlas en blanco.",
                color = Paleta.TextoSecundario,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonRom("Hacerla", Modifier.weight(1f), compacto = true, alTocar = alHacer)
                BotonRom("Después", Modifier.weight(1f), destacado = false, compacto = true, alTocar = alPosponer)
            }
        }
    }
}

/** Pantalla de la revisión semanal: 2 preguntas de texto libre y la lista de revisiones anteriores. */
@Composable
fun PantallaRevision(db: AppDatabase, alVolver: () -> Unit) {
    BackHandler(onBack = alVolver)
    val scope = rememberCoroutineScope()
    val semana = remember { lunesDe(LocalDate.now().toEpochDay()) }

    var barrera by rememberSaveable { mutableStateOf("") }
    var cambio by rememberSaveable { mutableStateOf("") }
    var anteriores by remember { mutableStateOf<List<Revision>>(emptyList()) }

    LaunchedEffect(Unit) { anteriores = revisionesRecientes(db.revisionDao().todas()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Volver") }
        Text("Revisión de la semana", style = MaterialTheme.typography.titleLarge)
        Text(
            "Sin culpas: es solo para entender qué te frena y ajustar tu entorno. Las dos respuestas son opcionales.",
            color = Paleta.TextoSecundario,
        )

        TarjetaRom(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("¿Qué te frenó esta semana?", style = MaterialTheme.typography.titleMedium)
                CampoRom(
                    value = barrera,
                    onValueChange = { barrera = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Por ejemplo: salí tarde del trabajo") },
                    minLines = 2,
                )
                Text("¿Qué podés cambiar en tu entorno?", style = MaterialTheme.typography.titleMedium)
                CampoRom(
                    value = cambio,
                    onValueChange = { cambio = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Por ejemplo: dejar el bolso listo la noche anterior") },
                    minLines = 2,
                )
                BotonRom(
                    "Guardar",
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = barrera.isNotBlank() || cambio.isNotBlank(),
                ) {
                    scope.launch {
                        // Una revisión por semana: si ya había una, se reemplaza.
                        db.revisionDao().guardar(Revision(semana, barrera.trim(), cambio.trim()))
                        alVolver()
                    }
                }
            }
        }

        if (anteriores.isNotEmpty()) {
            Text("Revisiones anteriores", style = MaterialTheme.typography.titleMedium)
            anteriores.forEach { r ->
                TarjetaRom(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val d = LocalDate.ofEpochDay(r.semana)
                        Text("Semana del ${d.dayOfMonth}/${d.monthValue}", style = MaterialTheme.typography.labelLarge, color = Paleta.Titulo)
                        if (r.barrera.isNotBlank()) Text("Me frenó: ${r.barrera}")
                        if (r.cambio.isNotBlank()) Text("Puedo cambiar: ${r.cambio}")
                    }
                }
            }
        }
    }
}
