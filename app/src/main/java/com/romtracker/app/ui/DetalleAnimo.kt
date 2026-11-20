package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Animo
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.logic.CARITAS
import com.romtracker.app.logic.NOMBRES_ANIMO
import com.romtracker.app.logic.animoEditable
import com.romtracker.app.logic.caritaDe
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/** Calendario mensual del ánimo: una carita por día; editable hoy y los 7 días anteriores. */
@Composable
fun DetalleAnimo(db: AppDatabase, alVolver: () -> Unit) {
    BackHandler(onBack = alVolver)

    val hoy = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()
    var animos by remember { mutableStateOf<Map<LocalDate, Int>>(emptyMap()) }
    var mes by remember { mutableStateOf(YearMonth.from(hoy)) }

    suspend fun cargar() {
        animos = db.animoDao().todos().associate { LocalDate.ofEpochDay(it.fecha) to it.valor }
    }

    LaunchedEffect(Unit) { cargar() }

    fun guardar(fecha: LocalDate, valor: Int?) {
        scope.launch {
            if (valor == null) db.animoDao().borrar(fecha.toEpochDay())
            else db.animoDao().guardar(Animo(fecha.toEpochDay(), valor))
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
        TextButton(onClick = alVolver) { Text("← ÁNIMO", style = MaterialTheme.typography.titleLarge) }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(onClick = { mes = mes.minusMonths(1) }) { Text("‹") }
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
                                        valor = null,
                                        esHoy = fecha == hoy,
                                        editable = animoEditable(fecha, hoy),
                                        futuro = fecha.isAfter(hoy),
                                        textoCelda = caritaDe(animos[fecha]),
                                        opciones = CARITAS.mapIndexed { i, c ->
                                            "$c ${NOMBRES_ANIMO[i]}" to { guardar(fecha, i + 1) }
                                        } + ("Sin dato" to { guardar(fecha, null) }),
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    "Una carita por día · · sin dato. Podés completar hoy y los 7 días anteriores.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
