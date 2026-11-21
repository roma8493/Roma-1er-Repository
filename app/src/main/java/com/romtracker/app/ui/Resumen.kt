package com.romtracker.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.data.claveRegistroPropio
import com.romtracker.app.logic.caritaDe
import com.romtracker.app.logic.cumplidoAgua
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")
private const val DIAS_VISIBLES = 7
/** Ancho de cada columna de día: 7 columnas + nombre deben entrar en el A34 sin scroll horizontal. */
private val ANCHO_DIA = 36.dp

/** Una celda del cuadro: texto (vacío = sin dato) y su color. */
private data class Celda(val texto: String = "", val tono: Tono = Tono.NINGUNO)

/** El color real se elige al dibujar (dentro de un @Composable) para seguir el tema claro/oscuro. */
private enum class Tono { NINGUNO, OK, TENUE, ATENCION }

/** Una fila del cuadro. [clave] null = todavía no se puede abrir su Detalle. */
private data class Fila(val titulo: String, val clave: String?, val celda: (LocalDate) -> Celda)

private fun celdaSiNo(valor: Boolean?): Celda = when (valor) {
    true -> Celda("✓", Tono.OK)
    false -> Celda("✕", Tono.TENUE)
    null -> Celda()
}

/**
 * Pantalla Resumen: cuadro con todos los hábitos (filas) y los últimos días (columnas),
 * para verlos de un vistazo. Solo lectura: para corregir un día se toca el nombre y se abre su Detalle.
 */
@Composable
fun Resumen(
    db: AppDatabase,
    alAbrir: (String) -> Unit,
    alVolver: () -> Unit,
) {
    val hoy = remember { LocalDate.now() }
    var filas by remember { mutableStateOf<List<Fila>?>(null) }
    // 0 = la ventana termina hoy; cada paso atrás retrocede DIAS_VISIBLES días.
    var pagina by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val habitos = db.habitoDao().todos().associateBy { it.tipo }
        val registros = db.registroDao().todosLosRegistros()
            .groupBy({ it.habito }, { LocalDate.ofEpochDay(it.fecha) to it.cumplido })
            .mapValues { it.value.toMap() }
        val aguas = db.aguaDao().todas().associateBy { LocalDate.ofEpochDay(it.fecha) }
        val animos = db.animoDao().todos().associate { LocalDate.ofEpochDay(it.fecha) to it.valor }

        fun filaDe(titulo: String, clave: String, abrible: Boolean) = Fila(
            titulo, if (abrible) clave else null,
        ) { dia -> celdaSiNo(registros[clave]?.get(dia)) }

        val lista = mutableListOf<Fila>()
        lista += filaDe("Gym", TipoHabito.GYM, habitos.containsKey(TipoHabito.GYM))
        lista += filaDe("Comida", TipoHabito.COMIDA, habitos.containsKey(TipoHabito.COMIDA))
        lista += Fila("Agua", if (habitos.containsKey(TipoHabito.AGUA)) TipoHabito.AGUA else null) { dia ->
            val a = aguas[dia]
            when {
                a == null -> Celda()
                cumplidoAgua(a.vasos, a.meta) -> Celda("✓", Tono.OK)
                else -> Celda(a.vasos.toString(), Tono.ATENCION)
            }
        }
        db.habitoPropioDao().todos().forEach { p ->
            val clave = claveRegistroPropio(p.id)
            // En DEJAR el ✓ guardado ya significa "ese día no lo hice".
            lista += filaDe(if (p.tipo == TipoPropio.DEJAR) "${p.nombre} (dejar)" else p.nombre, clave, true)
        }
        lista += Fila("Ánimo", "ANIMO") { dia -> Celda(caritaDe(animos[dia]) ?: "") }
        filas = lista
    }

    // Columnas: de la más antigua (izquierda) a la más nueva (derecha).
    val ultimo = hoy.minusDays((pagina * DIAS_VISIBLES).toLong())
    val dias = (DIAS_VISIBLES - 1 downTo 0).map { ultimo.minusDays(it.toLong()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) {
            Text("← Resumen", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { pagina++ }) { Text("‹ Antes") }
            Text(
                "${dias.first().dayOfMonth}/${dias.first().monthValue} – ${dias.last().dayOfMonth}/${dias.last().monthValue}",
                style = MaterialTheme.typography.labelMedium,
                color = Paleta.TextoSecundario,
            )
            TextButton(onClick = { pagina-- }, enabled = pagina > 0) { Text("Después ›") }
        }

        TarjetaRom(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                // Encabezado con los días
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f))
                    dias.forEach { d ->
                        val nombre = d.dayOfWeek.getDisplayName(TextStyle.SHORT, LOCALE_ES)
                            .take(3).uppercase()
                        Text(
                            "$nombre\n${d.dayOfMonth}",
                            modifier = Modifier.width(ANCHO_DIA),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (d == hoy) Paleta.Acento else Paleta.TextoSecundario,
                            fontWeight = if (d == hoy) FontWeight.Bold else null,
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Paleta.Pista)

                val lista = filas
                if (lista == null) {
                    Text("Cargando…", color = Paleta.TextoSecundario)
                } else {
                    lista.forEachIndexed { i, fila ->
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                fila.titulo,
                                modifier = Modifier
                                    .weight(1f)
                                    .then(if (fila.clave != null) Modifier.clickable { alAbrir(fila.clave) } else Modifier)
                                    .padding(end = 4.dp, top = 14.dp, bottom = 14.dp),
                                style = MaterialTheme.typography.titleSmall,
                                color = Paleta.Titulo,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            dias.forEach { d ->
                                val c = fila.celda(d)
                                val colorCelda = when (c.tono) {
                                    Tono.OK -> Paleta.Verde
                                    Tono.TENUE -> Paleta.Tenue
                                    Tono.ATENCION -> Paleta.Ambar
                                    Tono.NINGUNO -> Color.Unspecified
                                }
                                Box(Modifier.width(ANCHO_DIA), contentAlignment = Alignment.Center) {
                                    Text(
                                        c.texto,
                                        color = colorCelda,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                        if (i < lista.lastIndex) HorizontalDivider(color = Paleta.Pista)
                    }
                }
            }
        }

        Text(
            "✓ cumplido · ✕ no · vacío = sin dato. En Agua, el número son los vasos que tomaste. " +
                "En los hábitos que querés dejar, ✓ = ese día no lo hiciste. Tocá un nombre para ver su detalle.",
            style = MaterialTheme.typography.bodySmall,
            color = Paleta.TextoSecundario,
        )
    }
}

