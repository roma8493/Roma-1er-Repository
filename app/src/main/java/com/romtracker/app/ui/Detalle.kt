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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.Agua
import com.romtracker.app.data.Antojo
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Automatismo
import com.romtracker.app.data.HabitoPropio
import com.romtracker.app.data.PREFIJO_PROPIO
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.logic.Fase
import com.romtracker.app.logic.Registros
import com.romtracker.app.logic.ajustarVasos
import com.romtracker.app.logic.antojosDeHoy
import com.romtracker.app.logic.antojosDeLaSemana
import com.romtracker.app.logic.antojosTotales
import com.romtracker.app.logic.checksDeHabito
import com.romtracker.app.logic.consistencia
import com.romtracker.app.logic.diaDelHabito
import com.romtracker.app.logic.esEditable
import com.romtracker.app.logic.estadoDeFase
import com.romtracker.app.logic.rachaActual
import com.romtracker.app.logic.registrosDeAgua
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/** Pantalla P3: detalle de un hábito (GYM, COMIDA, AGUA o propio "PROPIO_<id>") con calendario mensual. */
@Composable
fun Detalle(db: AppDatabase, perfil: Perfil, tipo: String, alVolver: () -> Unit) {
    BackHandler(onBack = alVolver)

    val hoy = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()
    val esGym = tipo == TipoHabito.GYM
    val esAgua = tipo == TipoHabito.AGUA
    val esPropio = tipo.startsWith(PREFIJO_PROPIO)
    var propio by remember { mutableStateOf<HabitoPropio?>(null) }
    var editando by rememberSaveable { mutableStateOf(false) }
    var borrando by rememberSaveable { mutableStateOf(false) }
    val dejar = propio?.tipo == TipoPropio.DEJAR
    val titulo = when {
        esPropio -> propio?.nombre ?: "HÁBITO"
        tipo == TipoHabito.GYM -> "GYM"
        tipo == TipoHabito.AGUA -> "AGUA"
        else -> "COMIDA"
    }

    var inicio by remember { mutableStateOf<LocalDate?>(null) }
    var registros by remember { mutableStateOf<Registros>(emptyMap()) }
    var aguas by remember { mutableStateOf<Map<LocalDate, Agua>>(emptyMap()) }
    var mes by remember { mutableStateOf(YearMonth.from(hoy)) }
    var checks by remember { mutableStateOf<List<Automatismo>>(emptyList()) }
    var antojos by remember { mutableStateOf<List<Antojo>>(emptyList()) }
    // Día de desliz recién marcado en un hábito DEJAR: abre el diálogo opcional de desencadenante (R3).
    var deslizFecha by remember { mutableStateOf<LocalDate?>(null) }

    suspend fun cargar() {
        checks = db.automatismoDao().todos()
        antojos = db.antojoDao().todos()
        if (esPropio) {
            // Hábito propio: los registros guardan el ✓ ya resuelto según el tipo.
            val id = tipo.removePrefix(PREFIJO_PROPIO).toLongOrNull()
            val p = db.habitoPropioDao().todos().firstOrNull { it.id == id } ?: return
            propio = p
            inicio = LocalDate.ofEpochDay(p.fechaInicio)
            registros = db.registroDao().todos(tipo)
                .associate { LocalDate.ofEpochDay(it.fecha) to it.cumplido }
            return
        }
        val h = db.habitoDao().obtener(tipo) ?: return
        inicio = LocalDate.ofEpochDay(h.fechaInicio)
        if (esAgua) {
            val lista = db.aguaDao().todas()
            aguas = lista.associateBy { LocalDate.ofEpochDay(it.fecha) }
            registros = registrosDeAgua(lista)
        } else {
            registros = db.registroDao().todos(tipo)
                .associate { LocalDate.ofEpochDay(it.fecha) to it.cumplido }
        }
    }

    LaunchedEffect(Unit) { cargar() }

    fun marcar(fecha: LocalDate, valor: Boolean?) {
        val previo = registros[fecha]
        scope.launch {
            if (valor == null) db.registroDao().borrar(tipo, fecha.toEpochDay())
            else db.registroDao().guardar(Registro(tipo, fecha.toEpochDay(), valor))
            propio?.let { p ->
                if (valor != false) db.borrarDesencadenanteDelDia(p.id, fecha.toEpochDay())
                // Desliz nuevo (antes ✓ o sin dato): se ofrece anotar qué pasó; si ya era desliz no se vuelve a preguntar.
                else if (p.tipo == TipoPropio.DEJAR && previo != false) deslizFecha = fecha
            }
            cargar()
        }
    }

    /** Agua: suma o resta vasos a un día; el día nuevo usa la meta del perfil, el existente conserva la suya. */
    fun cambiarVasos(fecha: LocalDate, delta: Int) {
        scope.launch {
            val actual = aguas[fecha]
            val nuevos = ajustarVasos(actual?.vasos ?: 0, delta)
            db.registrarAgua(fecha.toEpochDay(), nuevos, actual?.meta ?: perfil.metaAgua)
            cargar()
        }
    }

    fun borrarAgua(fecha: LocalDate) {
        scope.launch {
            db.aguaDao().borrar(fecha.toEpochDay())
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
        TextButton(onClick = alVolver) {
            Text("← $titulo", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto)
        }

        val ini = inicio
        if (ini == null) {
            Text("Todavía no hay registros. Cuando registres tu primer día, aparece acá.")
            return@Column
        }

        val dia = diaDelHabito(ini, hoy)
        val e = estadoDeFase(dia)
        val cons = consistencia(ini, hoy, registros)
        val racha = rachaActual(ini, hoy, registros)

        TarjetaRom(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val nombreFase = when (e.fase) {
                    Fase.ADQUISICION -> "Adquisición"
                    Fase.CONSOLIDACION -> "Consolidación"
                    Fase.AUTOMATICO -> "Automático"
                }
                Text("FASE", style = MaterialTheme.typography.labelSmall, color = Paleta.TextoSecundario)
                Text("$nombreFase · día $dia", style = MaterialTheme.typography.titleLarge)
                if (e.metaSiguiente != null && e.diasParaSiguiente != null) {
                    BarraRom((dia.toFloat() / e.metaSiguiente).coerceIn(0f, 1f))
                    val siguiente = if (e.fase == Fase.ADQUISICION) "Consolidación" else "Automático"
                    Text(
                        "Falta${if (e.diasParaSiguiente == 1) " 1 día" else "n ${e.diasParaSiguiente} días"} para $siguiente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Paleta.TextoSecundario,
                    )
                } else {
                    Text("Automático 🎉", style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
                }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Estadistica("CONSISTENCIA", "${cons.cumplidos}", "de ${cons.total} días", Modifier.weight(1f))
                    Estadistica("RACHA", "$racha", if (racha == 1) "día" else "días", Modifier.weight(1f))
                }
            }
        }

        TarjetaRom(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = { mes = mes.minusMonths(1) },
                        enabled = mes.isAfter(YearMonth.from(ini)),
                    ) { Text("‹", style = MaterialTheme.typography.titleLarge) }
                    Text(
                        mes.month.getDisplayName(TextStyle.FULL, LOCALE_ES)
                            .replaceFirstChar { it.uppercase() } + " ${mes.year}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    TextButton(
                        onClick = { mes = mes.plusMonths(1) },
                        enabled = mes.isBefore(YearMonth.from(hoy)),
                    ) { Text("›", style = MaterialTheme.typography.titleLarge) }
                }
                Row(Modifier.fillMaxWidth()) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                        Text(
                            it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium, color = Paleta.TextoSecundario,
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
                                    val agua = aguas[fecha]
                                    CeldaDia(
                                        fecha = fecha,
                                        valor = registros[fecha],
                                        esHoy = fecha == hoy,
                                        editable = esEditable(fecha, ini, hoy),
                                        futuro = fecha.isAfter(hoy),
                                        // Agua: la celda muestra "vasos/meta" en vez de ✓/✗.
                                        textoCelda = if (esAgua && agua != null) "${agua.vasos}/${agua.meta}" else null,
                                        opciones = if (esAgua) listOf(
                                            "+1 vaso" to { cambiarVasos(fecha, 1) },
                                            "−1 vaso" to { cambiarVasos(fecha, -1) },
                                            "Sin dato" to { borrarAgua(fecha) },
                                        ) else listOf(
                                            // Propio: la 1.ª opción es siempre el ✓ (en DEJAR = "No lo hice").
                                            when {
                                                esPropio -> if (dejar) "No lo hice" else "Lo hice"
                                                esGym -> "Fui"
                                                else -> "Sano"
                                            } to { marcar(fecha, true) },
                                            when {
                                                esPropio -> if (dejar) "Lo hice" else "No lo hice"
                                                esGym -> "No fui"
                                                else -> "Chatarra"
                                            } to { marcar(fecha, false) },
                                            "Sin dato" to { marcar(fecha, null) },
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
                val leyenda = when {
                    esPropio && dejar -> "✓ no lo hice · ✗ lo hice · · sin dato. "
                    esPropio -> "✓ lo hice · ✗ no lo hice · · sin dato. "
                    esAgua -> "Cada día muestra vasos/meta. Fondo marcado = llegaste a la meta · · sin dato. "
                    esGym -> "✓ fui · ✗ no fui · · sin dato. "
                    else -> "✓ sano · ✗ chatarra · · sin dato. "
                }
                Text(
                    leyenda + "Podés completar hoy y los 7 días anteriores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Paleta.TextoSecundario,
                )
            }
        }

        val p = propio
        if (esPropio && p != null) {
            TarjetaRom(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Después de ${p.claveAncla}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (dejar) "Quiero dejar: ${p.nombre}" else "Quiero hacer: ${p.nombre}",
                        color = Paleta.TextoSecundario,
                    )
                    Text("¿Por qué? ${p.porQue}", color = Paleta.TextoSecundario)
                    if (p.versionMinima.isNotBlank()) Text("Versión mínima: ${p.versionMinima}", color = Paleta.TextoSecundario)
                    if (dejar) {
                        BloqueAntojo(
                            hoy = antojosDeHoy(antojos, p.id, hoy.toEpochDay()),
                            semana = antojosDeLaSemana(antojos, p.id, hoy.toEpochDay()),
                            total = antojosTotales(antojos, p.id),
                            alSumar = { scope.launch { db.sumarAntojo(p.id, hoy.toEpochDay()); cargar() } },
                            alRestar = { scope.launch { db.restarAntojo(p.id, hoy.toEpochDay()); cargar() } },
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonSecundario("Editar") { editando = true }
                        BotonSecundario("Borrar hábito") { borrando = true }
                    }
                }
            }
            if (editando) {
                DialogoEditarPropio(
                    p,
                    alCancelar = { editando = false },
                    alGuardar = { nombre, ancla, porQue, versionMinima ->
                        scope.launch {
                            db.habitoPropioDao().actualizar(p.copy(nombre = nombre, claveAncla = ancla, porQue = porQue, versionMinima = versionMinima))
                            editando = false
                            cargar()
                        }
                    },
                )
            }
            deslizFecha?.let { f ->
                DialogoDesencadenante(
                    alGuardar = { lugar, momento, emocion, compania ->
                        deslizFecha = null
                        scope.launch { db.anotarDesencadenante(p.id, f.toEpochDay(), lugar, momento, emocion, compania) }
                    },
                    alCerrar = { deslizFecha = null },
                )
            }
            if (borrando) {
                AlertDialog(
                    onDismissRequest = { borrando = false },
                    title = { Text("¿Borrar \"${p.nombre}\"?") },
                    text = { Text("Se borra el hábito y todo su historial (calendario, fase y racha). No se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                db.borrarPropio(p.id)
                                borrando = false
                                alVolver()
                            }
                        }) { Text("Borrar") }
                    },
                    dismissButton = { TextButton(onClick = { borrando = false }) { Text("Cancelar") } },
                )
            }
        }

        if (esGym) {
            TarjetaRom(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Después de ${perfil.claveAncla}, voy al gym.", style = MaterialTheme.typography.titleMedium)
                    Text("¿Por qué? ${perfil.porQue}", color = Paleta.TextoSecundario)
                    if (perfil.versionMinima.isNotBlank()) Text("Versión mínima: ${perfil.versionMinima}", color = Paleta.TextoSecundario)
                }
            }
        }

        // R6: el check de automatismo es solo para hábitos HACER (no para los propios de tipo DEJAR).
        if (!esPropio || (propio != null && !dejar)) {
            TarjetaAutomatismoDetalle(checksDeHabito(tipo, checks))
        }
    }
}

@Composable
internal fun CeldaDia(
    fecha: LocalDate,
    valor: Boolean?,
    esHoy: Boolean,
    editable: Boolean,
    futuro: Boolean,
    textoCelda: String?,
    opciones: List<Pair<String, () -> Unit>>,
) {
    var menu by remember { mutableStateOf(false) }
    val fondo = when (valor) {
        true -> Paleta.Verde.copy(alpha = 0.16f)
        false -> Paleta.Rojo.copy(alpha = 0.14f)
        null -> Paleta.Fondo
    }
    val colorSimbolo = when (valor) {
        true -> Paleta.Verde
        false -> Paleta.Rojo
        null -> Paleta.TextoSecundario
    }
    val simbolo = when {
        futuro -> ""
        textoCelda != null -> textoCelda
        valor == true -> "✓"
        valor == false -> "✗"
        else -> "·"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .alpha(if (editable || futuro) 1f else 0.4f)
            .background(if (futuro) Paleta.Tarjeta else fondo, RoundedCornerShape(12.dp))
            .then(if (esHoy) Modifier.border(BorderStroke(2.dp, Paleta.Acento), RoundedCornerShape(12.dp)) else Modifier)
            .clickable(enabled = editable) { menu = true },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${fecha.dayOfMonth}", style = MaterialTheme.typography.labelSmall, color = Paleta.TextoSecundario)
            Text(
                simbolo,
                style = if (textoCelda != null) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium,
                color = if (textoCelda != null) Paleta.Texto else colorSimbolo,
            )
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            opciones.forEach { (texto, accion) ->
                DropdownMenuItem(text = { Text(texto) }, onClick = { menu = false; accion() })
            }
        }
    }
}

/** Diálogo para editar nombre, ancla y "por qué" de un hábito propio (el tipo y el inicio no cambian). */
@Composable
private fun DialogoEditarPropio(
    p: HabitoPropio,
    alCancelar: () -> Unit,
    alGuardar: (nombre: String, ancla: String, porQue: String, versionMinima: String) -> Unit,
) {
    var nombre by rememberSaveable { mutableStateOf(p.nombre) }
    var ancla by rememberSaveable { mutableStateOf(p.claveAncla) }
    var porQue by rememberSaveable { mutableStateOf(p.porQue) }
    var versionMinima by rememberSaveable { mutableStateOf(p.versionMinima) }
    val valido = nombre.isNotBlank() && ancla.isNotBlank() && porQue.isNotBlank()
    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Editar hábito") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CampoRom(nombre, { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                CampoRom(ancla, { ancla = it }, label = { Text("Clave ancla: Después de…") }, modifier = Modifier.fillMaxWidth())
                CampoRom(porQue, { porQue = it }, label = { Text("¿Por qué es importante?") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                CampoRom(versionMinima, { versionMinima = it }, label = { Text("Versión mínima (opcional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = valido, onClick = { alGuardar(nombre.trim(), ancla.trim(), porQue.trim(), versionMinima.trim()) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } },
    )
}

/** Etiqueta pequeña, número grande y unidad: para consistencia y racha. */
@Composable
private fun Estadistica(etiqueta: String, numero: String, unidad: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = Paleta.TextoSecundario)
        Text(numero, style = MaterialTheme.typography.headlineMedium)
        Text(unidad, style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
    }
}

/** Botón redondeado de contorno suave (alto ≥ 48 dp), igual al de Hoy. */
@Composable
private fun BotonSecundario(texto: String, alTocar: () -> Unit) {
    OutlinedButton(
        onClick = alTocar,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
        border = BorderStroke(1.dp, Paleta.Borde),
    ) { Text(texto) }
}
