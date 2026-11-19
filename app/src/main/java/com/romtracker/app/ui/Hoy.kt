package com.romtracker.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Checklist
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.logic.Fase
import com.romtracker.app.logic.diaDelHabito
import com.romtracker.app.logic.estadoDeFase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/** Estado de un hábito para pintar su tarjeta. [hoy]: null = sin dato. */
private data class TarjetaEstado(val dia: Int, val hoy: Boolean?)

/**
 * Pantalla P2 (Hoy): tarjetas Gym y Comida con registro en 1 toque (tocar el otro botón corrige),
 * y la checklist de la noche ("para mañana") + lo que se preparó anoche.
 */
@Composable
fun Hoy(db: AppDatabase, perfil: Perfil, alAbrir: (String) -> Unit, alAbrirCopia: () -> Unit) {
    val fecha = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()

    var gym by remember { mutableStateOf<TarjetaEstado?>(null) }
    var comida by remember { mutableStateOf<TarjetaEstado?>(null) }
    var anoche by remember { mutableStateOf<Checklist?>(null) }
    var paraManana by remember { mutableStateOf(Checklist(fecha.plusDays(1).toEpochDay())) }

    suspend fun cargarHabito(tipo: String): TarjetaEstado? {
        val habito = db.habitoDao().obtener(tipo) ?: return null
        val registroHoy = db.registroDao().todos(tipo)
            .firstOrNull { it.fecha == fecha.toEpochDay() }?.cumplido
        return TarjetaEstado(diaDelHabito(LocalDate.ofEpochDay(habito.fechaInicio), fecha), registroHoy)
    }

    LaunchedEffect(Unit) {
        gym = cargarHabito(TipoHabito.GYM)
        comida = cargarHabito(TipoHabito.COMIDA)
        anoche = db.checklistDao().obtener(fecha.toEpochDay())
        paraManana = db.checklistDao().obtener(fecha.plusDays(1).toEpochDay()) ?: paraManana
    }

    fun registrar(tipo: String, cumplido: Boolean) {
        scope.launch {
            // La comida empieza con el primer registro (SPEC); no pisa el inicio existente.
            if (tipo == TipoHabito.COMIDA) {
                db.habitoDao().crear(com.romtracker.app.data.Habito(tipo, fecha.toEpochDay()))
            }
            db.registroDao().guardar(Registro(tipo, fecha.toEpochDay(), cumplido))
            val nuevo = cargarHabito(tipo)
            if (tipo == TipoHabito.GYM) gym = nuevo else comida = nuevo
        }
    }

    fun guardarChecklist(nueva: Checklist) {
        paraManana = nueva
        scope.launch { db.checklistDao().guardar(nueva) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val diaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, LOCALE_ES)
            .replaceFirstChar { it.uppercase() }
        val mes = fecha.month.getDisplayName(TextStyle.SHORT, LOCALE_ES)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Hola ${perfil.nombre} · $diaSemana ${fecha.dayOfMonth} $mes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = alAbrirCopia) { Text("Copia") }
        }

        TarjetaHabito(
            titulo = "GYM",
            estado = gym,
            pregunta = "¿Vamos hoy?",
            textoSi = "Fui ✓",
            textoNo = "Hoy no",
            mensajeNo = "Mañana es otra oportunidad.",
            mensajeSi = "¡Qué bueno que fuiste!",
            alAbrir = { alAbrir(TipoHabito.GYM) },
            alElegir = { registrar(TipoHabito.GYM, it) },
        )

        TarjetaHabito(
            titulo = "COMIDA",
            estado = comida,
            pregunta = "¿Cómo viene el día?",
            textoSi = "Sano",
            textoNo = "Chatarra",
            mensajeNo = "Un desliz no borra lo que venís haciendo.",
            mensajeSi = "Bien ahí, sigamos así.",
            alAbrir = { alAbrir(TipoHabito.COMIDA) },
            alElegir = { registrar(TipoHabito.COMIDA, it) },
        )

        if (anoche != null) {
            val a = anoche!!
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Lo que preparaste anoche:", style = MaterialTheme.typography.titleMedium)
                    FilaCheck("Bolso del gym listo", a.bolso, null)
                    FilaCheck("Comida preparada", a.comida, null)
                    FilaCheck("Decidí a qué hora voy", a.hora, null)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Para mañana (noche):", style = MaterialTheme.typography.titleMedium)
                FilaCheck("Bolso del gym listo", paraManana.bolso) {
                    guardarChecklist(paraManana.copy(bolso = it))
                }
                FilaCheck("Comida preparada", paraManana.comida) {
                    guardarChecklist(paraManana.copy(comida = it))
                }
                FilaCheck("Decidí a qué hora voy", paraManana.hora) {
                    guardarChecklist(paraManana.copy(hora = it))
                }
            }
        }
    }
}

@Composable
private fun TarjetaHabito(
    titulo: String,
    estado: TarjetaEstado?,
    pregunta: String,
    textoSi: String,
    textoNo: String,
    mensajeSi: String,
    mensajeNo: String,
    alAbrir: () -> Unit,
    alElegir: (Boolean) -> Unit,
) {
    // Tocar la tarjeta abre el Detalle (solo si el hábito ya empezó).
    Card(onClick = alAbrir, enabled = estado != null, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (estado == null) {
                // Solo la comida puede no existir todavía: empieza con el primer registro.
                Text(titulo, style = MaterialTheme.typography.titleMedium)
            } else {
                val e = estadoDeFase(estado.dia)
                val nombreFase = when (e.fase) {
                    Fase.ADQUISICION -> "Adquisición"
                    Fase.CONSOLIDACION -> "Consolidación"
                    Fase.AUTOMATICO -> "Automático"
                }
                Text("$titulo · $nombreFase día ${estado.dia}", style = MaterialTheme.typography.titleMedium)
                if (e.metaSiguiente != null) {
                    val avance = estado.dia.toFloat() / e.metaSiguiente
                    LinearProgressIndicator(
                        progress = { avance.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    )
                    Text("${estado.dia}/${e.metaSiguiente}", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("Automático 🎉", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text(pregunta)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val elegido = estado?.hoy
                BotonOpcion(textoSi, elegido == true, Modifier.weight(1f)) { alElegir(true) }
                BotonOpcion(textoNo, elegido == false, Modifier.weight(1f)) { alElegir(false) }
            }
            when (estado?.hoy) {
                true -> Text(mensajeSi, style = MaterialTheme.typography.bodyMedium)
                false -> Text(mensajeNo, style = MaterialTheme.typography.bodyMedium)
                null -> Unit
            }
            if (estado != null) {
                Text(
                    "Ver calendario y detalle ›",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun BotonOpcion(texto: String, elegido: Boolean, modifier: Modifier, alTocar: () -> Unit) {
    val m = modifier.heightIn(min = 56.dp)
    if (elegido) Button(onClick = alTocar, modifier = m) { Text(texto) }
    else OutlinedButton(onClick = alTocar, modifier = m) { Text(texto) }
}

/** [alCambiar] null = solo lectura. */
@Composable
private fun FilaCheck(texto: String, marcado: Boolean, alCambiar: ((Boolean) -> Unit)?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = marcado, onCheckedChange = alCambiar, enabled = alCambiar != null)
        Text(texto)
    }
}
