package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.logic.Fase
import com.romtracker.app.logic.Registros
import com.romtracker.app.logic.consistencia
import com.romtracker.app.logic.diaDelHabito
import com.romtracker.app.logic.esEditable
import com.romtracker.app.logic.estadoDeFase
import com.romtracker.app.logic.rachaActual
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/** Pantalla P3: detalle de un hábito (GYM o COMIDA) con calendario mensual. */
@Composable
fun Detalle(db: AppDatabase, perfil: Perfil, tipo: String, alVolver: () -> Unit) {
    BackHandler(onBack = alVolver)

    val hoy = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()
    val esGym = tipo == TipoHabito.GYM
    val titulo = if (esGym) "GYM" else "COMIDA"

    var inicio by remember { mutableStateOf<LocalDate?>(null) }
    var registros by remember { mutableStateOf<Registros>(emptyMap()) }
    var mes by remember { mutableStateOf(YearMonth.from(hoy)) }

    suspend fun cargar() {
        val h = db.habitoDao().obtener(tipo) ?: return
        inicio = LocalDate.ofEpochDay(h.fechaInicio)
        registros = db.registroDao().todos(tipo)
            .associate { LocalDate.ofEpochDay(it.fecha) to it.cumplido }
    }

    LaunchedEffect(Unit) { cargar() }

    fun marcar(fecha: LocalDate, valor: Boolean?) {
        scope.launch {
            if (valor == null) db.registroDao().borrar(tipo, fecha.toEpochDay())
            else db.registroDao().guardar(Registro(tipo, fecha.toEpochDay(), valor))
            cargar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← $titulo", style = MaterialTheme.typography.titleLarge) }

        val ini = inicio
        if (ini == null) {
            Text("Todavía no hay registros. Cuando registres tu primer día, aparece acá.")
            return@Column
        }

        val dia = diaDelHabito(ini, hoy)
        val e = estadoDeFase(dia)
        val cons = consistencia(ini, hoy, registros)
        val racha = rachaActual(ini, hoy, registros)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val nombreFase = when (e.fase) {
                    Fase.ADQUISICION -> "Adquisición"
                    Fase.CONSOLIDACION -> "Consolidación"
                    Fase.AUTOMATICO -> "Automático"
                }
                Text("Fase: $nombreFase · día $dia", style = MaterialTheme.typography.titleMedium)
                if (e.metaSiguiente != null && e.diasParaSiguiente != null) {
                    LinearProgressIndicator(
                        progress = { (dia.toFloat() / e.metaSiguiente).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    val siguiente = if (e.fase == Fase.ADQUISICION) "Consolidación" else "Automático"
                    Text("Faltan ${e.diasParaSiguiente} días para $siguiente")
                } else {
                    Text("Automático 🎉")
                }
                Text("Consistencia: ${cons.cumplidos} de ${cons.total}")
                Text("Racha actual: $racha ${if (racha == 1) "día" else "días"}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = { mes = mes.minusMonths(1) },
                        enabled = mes.isAfter(YearMonth.from(ini)),
                    ) { Text("‹") }
                    Text(
                        mes.month.getDisplayName(TextStyle.FULL, LOCALE_ES)
                            .replaceFirstChar { it.uppercase() } + " ${mes.year}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    TextButton(
                        onClick = { mes = mes.plusMonths(1) },
                        enabled = mes.isBefore(YearMonth.from(hoy)),
                    ) { Text("›") }
                }
                Row(Modifier.fillMaxWidth()) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                        Text(
                            it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                val vacios = mes.atDay(1).dayOfWeek.value - 1
                val celdas = vacios + mes.lengthOfMonth()
                for (fila in 0 until (celdas + 6) / 7) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (col in 0 until 7) {
                            val n = fila * 7 + col - vacios + 1
                            Box(Modifier.weight(1f)) {
                                if (n in 1..mes.lengthOfMonth()) {
                                    val fecha = mes.atDay(n)
                                    CeldaDia(
                                        fecha = fecha,
                                        valor = registros[fecha],
                                        esHoy = fecha == hoy,
                                        editable = esEditable(fecha, ini, hoy),
                                        futuro = fecha.isAfter(hoy),
                                        textoSi = if (esGym) "Fui" else "Sano",
                                        textoNo = if (esGym) "No fui" else "Chatarra",
                                        alElegir = { marcar(fecha, it) },
                                        alBorrar = { marcar(fecha, null) },
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    "✓ ${if (esGym) "fui" else "sano"} · ✗ ${if (esGym) "no fui" else "chatarra"} · · sin dato. " +
                        "Podés completar hoy y los 7 días anteriores.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (esGym) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Después de ${perfil.claveAncla}, voy al gym.", style = MaterialTheme.typography.titleMedium)
                    Text("¿Por qué? ${perfil.porQue}")
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    fecha: LocalDate,
    valor: Boolean?,
    esHoy: Boolean,
    editable: Boolean,
    futuro: Boolean,
    textoSi: String,
    textoNo: String,
    alElegir: (Boolean) -> Unit,
    alBorrar: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme
    val fondo = when (valor) {
        true -> colores.primaryContainer
        false -> colores.secondaryContainer
        null -> colores.surfaceVariant
    }
    val simbolo = when {
        futuro -> ""
        valor == true -> "✓"
        valor == false -> "✗"
        else -> "·"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .alpha(if (editable || futuro) 1f else 0.4f)
            .background(if (futuro) colores.surface else fondo, RoundedCornerShape(8.dp))
            .then(if (esHoy) Modifier.border(BorderStroke(2.dp, colores.primary), RoundedCornerShape(8.dp)) else Modifier)
            .clickable(enabled = editable) { menu = true },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${fecha.dayOfMonth}", style = MaterialTheme.typography.labelSmall)
            Text(simbolo, style = MaterialTheme.typography.bodyMedium)
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            DropdownMenuItem(text = { Text(textoSi) }, onClick = { menu = false; alElegir(true) })
            DropdownMenuItem(text = { Text(textoNo) }, onClick = { menu = false; alElegir(false) })
            DropdownMenuItem(text = { Text("Sin dato") }, onClick = { menu = false; alBorrar() })
        }
    }
}
