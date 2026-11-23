package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Automatismo
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.PREFIJO_PROPIO
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.logic.puntajeAutomatismo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

/** Prefijo de la "pantalla" del check dentro de `abierto` (MainActivity): "AUTO:" + clave del hábito. */
const val PREFIJO_AUTOMATISMO = "AUTO:"

/** Preguntas del check (R6, estilo SRHI). Escala 1–5; se habla de "qué tan automático se siente", nunca de nota. */
private val PREGUNTAS = listOf(
    "Lo hago sin tener que pensarlo.",
    "Lo hago sin darme cuenta de que ya empecé.",
    "Me resultaría raro no hacerlo.",
    "Me sale casi sin esfuerzo.",
)

/** Nombre que se muestra del hábito [clave] (AGUA o PROPIO_<id>). */
suspend fun nombreDeHabito(db: AppDatabase, clave: String): String = when {
    clave == TipoHabito.AGUA -> "Agua"
    clave.startsWith(PREFIJO_PROPIO) -> {
        val id = clave.removePrefix(PREFIJO_PROPIO).toLongOrNull()
        db.habitoPropioDao().todos().firstOrNull { it.id == id }?.nombre ?: "este hábito"
    }
    else -> "este hábito"
}

/** Pantalla de 4 preguntas con escala 1–5. Guardar queda habilitado cuando se respondieron las 4. */
@Composable
fun CheckAutomatismo(db: AppDatabase, clave: String, alVolver: () -> Unit) {
    BackHandler(onBack = alVolver)
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    LaunchedEffect(clave) { nombre = nombreDeHabito(db, clave) }

    // 0 = sin responder. rememberSaveable para no perder lo marcado al girar el celular.
    var r1 by rememberSaveable { mutableIntStateOf(0) }
    var r2 by rememberSaveable { mutableIntStateOf(0) }
    var r3 by rememberSaveable { mutableIntStateOf(0) }
    var r4 by rememberSaveable { mutableIntStateOf(0) }
    val respuestas = listOf(r1, r2, r3, r4)
    val completo = respuestas.all { it in 1..5 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Volver") }
        Text("Check de $nombre", style = MaterialTheme.typography.titleLarge)
        Text(
            "Cuatro frases para ver qué tan automático se siente. No hay respuestas buenas ni malas: " +
                "1 = nada, 5 = totalmente.",
            color = Paleta.TextoSecundario,
        )

        PREGUNTAS.forEachIndexed { i, texto ->
            TarjetaRom(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(texto, style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (valor in 1..5) {
                            BotonEscala(
                                texto = valor.toString(),
                                elegido = respuestas[i] == valor,
                                modifier = Modifier.weight(1f),
                            ) {
                                when (i) { 0 -> r1 = valor; 1 -> r2 = valor; 2 -> r3 = valor; else -> r4 = valor }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Nada", style = MaterialTheme.typography.labelSmall, color = Paleta.TextoSecundario)
                        Text("Totalmente", style = MaterialTheme.typography.labelSmall, color = Paleta.TextoSecundario)
                    }
                }
            }
        }

        BotonRom(
            "Guardar",
            modifier = Modifier.fillMaxWidth(),
            habilitado = completo,
        ) {
            scope.launch {
                db.automatismoDao().guardar(Automatismo(clave, LocalDate.now().toEpochDay(), r1, r2, r3, r4))
                alVolver()
            }
        }
    }
}

@Composable
private fun BotonEscala(texto: String, elegido: Boolean, modifier: Modifier, alTocar: () -> Unit) {
    val m = modifier.heightIn(min = 52.dp)
    if (elegido) Button(onClick = alTocar, modifier = m, contentPadding = PaddingValues(0.dp)) { Text(texto) }
    else OutlinedButton(
        onClick = alTocar,
        modifier = m,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
        border = BorderStroke(1.dp, Paleta.Borde),
    ) { Text(texto) }
}

/** Tarjeta de Hoy que invita al check. "Después" la oculta mientras la app siga abierta. */
@Composable
fun TarjetaInvitacionAutomatismo(nombre: String, alHacer: () -> Unit, alPosponer: () -> Unit) {
    TarjetaRom(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("¿Hacemos el check de $nombre?", style = MaterialTheme.typography.titleMedium)
            Text(
                "Son 4 frases para ver qué tan automático se siente. Lleva menos de un minuto.",
                color = Paleta.TextoSecundario,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonRom("Hacerlo", Modifier.weight(1f), compacto = true, alTocar = alHacer)
                BotonRom("Después", Modifier.weight(1f), destacado = false, compacto = true, alTocar = alPosponer)
            }
        }
    }
}

/** Tarjeta del Detalle: último puntaje y gráfico simple de evolución. [checks] va del más viejo al más nuevo. */
@Composable
fun TarjetaAutomatismoDetalle(checks: List<Automatismo>) {
    TarjetaRom(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Qué tan automático se siente", style = MaterialTheme.typography.titleMedium)
            if (checks.isEmpty()) {
                Text(
                    "Todavía no hay checks. Cada 4 semanas te invito a hacer uno desde Hoy.",
                    color = Paleta.TextoSecundario,
                )
            } else {
                val ultimo = puntajeAutomatismo(checks.last())
                Text(
                    String.format(Locale("es", "AR"), "%.1f de 5", ultimo),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Paleta.Titulo,
                )
                val dia = LocalDate.ofEpochDay(checks.last().fecha)
                Text(
                    "Último check: ${dia.dayOfMonth}/${dia.monthValue}/${dia.year}",
                    color = Paleta.TextoSecundario,
                )
                GraficoAutomatismo(checks.map { puntajeAutomatismo(it) })
                Text(
                    if (checks.size == 1) "Con el próximo check vas a ver cómo evoluciona."
                    else "${checks.size} checks, del más viejo al más nuevo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Paleta.TextoSecundario,
                )
            }
        }
    }
}

/** Línea simple de puntajes (eje de 1 a 5), un punto por check, repartidos parejo. */
@Composable
private fun GraficoAutomatismo(puntajes: List<Double>) {
    // Paleta.X es @Composable: se leen los colores fuera del lambda del Canvas.
    val colorGuia = Paleta.Borde
    val colorLinea = Paleta.Acento
    Canvas(Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp)) {
        val margen = 8.dp.toPx()
        val ancho = size.width - 2 * margen
        val alto = size.height
        fun yDe(p: Double) = alto - ((p.toFloat() - 1f) / 4f) * alto
        // Guías en 1, 3 y 5
        for (g in listOf(1.0, 3.0, 5.0)) {
            drawLine(colorGuia, Offset(margen, yDe(g)), Offset(margen + ancho, yDe(g)), strokeWidth = 1.dp.toPx())
        }
        fun xDe(i: Int) = if (puntajes.size == 1) margen + ancho / 2 else margen + ancho * i / (puntajes.size - 1)
        for (i in 0 until puntajes.size - 1) {
            drawLine(
                colorLinea, Offset(xDe(i), yDe(puntajes[i])), Offset(xDe(i + 1), yDe(puntajes[i + 1])),
                strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round,
            )
        }
        puntajes.forEachIndexed { i, p -> drawCircle(colorLinea, radius = 5.dp.toPx(), center = Offset(xDe(i), yDe(p))) }
    }
}
