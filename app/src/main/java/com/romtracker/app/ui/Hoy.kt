package com.romtracker.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import com.romtracker.app.data.Animo
import com.romtracker.app.data.Antojo
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.ChecklistItem
import com.romtracker.app.data.ChecklistMarca
import com.romtracker.app.data.HabitoPropio
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.Registro
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.data.TipoPropio
import com.romtracker.app.data.claveRegistroPropio
import com.romtracker.app.logic.CARITAS
import com.romtracker.app.logic.Fase
import com.romtracker.app.logic.NOMBRES_ANIMO
import com.romtracker.app.logic.ajustarVasos
import com.romtracker.app.logic.antojosDeHoy
import com.romtracker.app.logic.cumplidoAgua
import com.romtracker.app.logic.diaDelHabito
import com.romtracker.app.logic.estaHecho
import com.romtracker.app.logic.estadoDeFase
import com.romtracker.app.logic.habitosQueTocan
import com.romtracker.app.logic.cruzoFase
import com.romtracker.app.logic.falloAyer
import com.romtracker.app.logic.iconoDe
import com.romtracker.app.logic.mensajeCelebracion
import com.romtracker.app.logic.rachaActual
import com.romtracker.app.logic.textoAvisoFase
import com.romtracker.app.logic.textoNuncaDosVeces
import com.romtracker.app.logic.tocaRevision
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/**
 * Estado de un hábito para pintar su tarjeta. [hoy]: null = sin dato.
 * [racha]: racha actual (R14); [falloAyer]: ayer quedó en "no" (R13).
 */
private data class TarjetaEstado(val dia: Int, val hoy: Boolean?, val racha: Int = 0, val falloAyer: Boolean = false)

/** Estado del agua. [dia] null = el hábito todavía no empezó (empieza con el primer vaso). */
private data class AguaEstado(val dia: Int?, val vasos: Int, val meta: Int)

/**
 * Pantalla P2 (Hoy): tarjetas de los hábitos propios con registro en 1 toque (tocar el otro botón corrige),
 * la tarjeta Agua (+1 / −1 vaso) y la checklist de la noche ("para mañana") + lo que se preparó anoche.
 */
@Composable
fun Hoy(
    db: AppDatabase,
    perfil: Perfil,
    alAbrir: (String) -> Unit,
    alAbrirCopia: () -> Unit,
    alAbrirAjustes: () -> Unit,
    alNuevoHabito: () -> Unit,
    alAbrirResumen: () -> Unit,
    alHacerCheck: (String) -> Unit,
    pospuestos: String,
    alPosponerCheck: (String) -> Unit,
    alAbrirRevision: () -> Unit,
) {
    val fecha = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()

    var agua by remember { mutableStateOf<AguaEstado?>(null) }
    var animo by remember { mutableStateOf<Int?>(null) }
    // Hábitos propios con su estado de hoy (TarjetaEstado.hoy = ✓ ya resuelto según el tipo).
    var propios by remember { mutableStateOf<List<Pair<HabitoPropio, TarjetaEstado>>>(emptyList()) }
    // Desliz recién registrado en un hábito DEJAR: abre el diálogo opcional de desencadenante (R3).
    var deslizDe by remember { mutableStateOf<HabitoPropio?>(null) }
    // Antojos resistidos (R11) de los hábitos propios DEJAR.
    var antojos by remember { mutableStateOf<List<Antojo>>(emptyList()) }
    var cargado by remember { mutableStateOf(false) }
    var editandoChecklist by remember { mutableStateOf(false) }
    // Checklist editable: ítems actuales y marcas de anoche (para hoy) y de esta noche (para mañana).
    var itemsChecklist by remember { mutableStateOf<List<ChecklistItem>>(emptyList()) }
    var anoche by remember { mutableStateOf<List<ChecklistMarca>>(emptyList()) }
    var paraManana by remember { mutableStateOf<List<ChecklistMarca>>(emptyList()) }
    // Hábitos HACER a los que hoy les toca el check de automatismo: clave → nombre (R6).
    // ¿Toca la revisión semanal (R9)?
    var tocaRev by remember { mutableStateOf(false) }
    var tocaCheck by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    suspend fun cargarAgua(): AguaEstado {
        val habito = db.habitoDao().obtener(TipoHabito.AGUA)
        val fila = db.aguaDao().todas().firstOrNull { it.fecha == fecha.toEpochDay() }
        return AguaEstado(
            dia = habito?.let { diaDelHabito(LocalDate.ofEpochDay(it.fechaInicio), fecha) },
            vasos = fila?.vasos ?: 0,
            // Si hoy ya tiene fila, esa meta; si no, la del perfil (Ajustes).
            meta = fila?.meta ?: perfil.metaAgua,
        )
    }

    suspend fun cargarPropios(): List<Pair<HabitoPropio, TarjetaEstado>> =
        db.habitoPropioDao().todos().map { p ->
            val inicio = LocalDate.ofEpochDay(p.fechaInicio)
            val registros = db.registroDao().todos(claveRegistroPropio(p.id))
                .associate { LocalDate.ofEpochDay(it.fecha) to it.cumplido }
            p to TarjetaEstado(
                dia = diaDelHabito(inicio, fecha),
                hoy = registros[fecha],
                racha = rachaActual(inicio, fecha, registros),
                falloAyer = falloAyer(inicio, fecha, registros),
            )
        }

    fun registrarPropio(p: HabitoPropio, cumplido: Boolean) {
        scope.launch {
            val clave = claveRegistroPropio(p.id)
            val previo = db.registroDao().todos(clave).firstOrNull { it.fecha == fecha.toEpochDay() }?.cumplido
            db.registroDao().guardar(Registro(clave, fecha.toEpochDay(), cumplido))
            if (cumplido) db.borrarDesencadenanteDelDia(p.id, fecha.toEpochDay())
            // Desliz nuevo en un hábito DEJAR: se ofrece anotar qué pasó (opcional).
            else if (p.tipo == TipoPropio.DEJAR && previo != false) deslizDe = p
            propios = cargarPropios()
        }
    }

    fun cambiarAntojo(p: HabitoPropio, delta: Int) {
        scope.launch {
            if (delta > 0) db.sumarAntojo(p.id, fecha.toEpochDay()) else db.restarAntojo(p.id, fecha.toEpochDay())
            antojos = db.antojoDao().todos()
        }
    }

    LaunchedEffect(Unit) {
        antojos = db.antojoDao().todos()
        propios = cargarPropios()
        cargado = true
        agua = cargarAgua()
        animo = db.animoDao().todos().firstOrNull { it.fecha == fecha.toEpochDay() }?.valor
        itemsChecklist = db.checklistItemDao().todos()
        anoche = db.checklistMarcaDao().deDia(fecha.toEpochDay())
        paraManana = db.checklistMarcaDao().deDia(fecha.plusDays(1).toEpochDay())

        // Check de automatismo: solo hábitos HACER (Agua y propios HACER); no Ánimo ni DEJAR.
        val hacer = listOfNotNull(
            db.habitoDao().obtener(TipoHabito.AGUA)?.takeIf { perfil.aguaActiva }
                ?.let { Triple(TipoHabito.AGUA, "Agua", it.fechaInicio) },
        ) + propios.filter { (p, _) -> p.tipo == TipoPropio.HACER }
            .map { (p, _) -> Triple(claveRegistroPropio(p.id), p.nombre, p.fechaInicio) }
        val claves = habitosQueTocan(
            hacer.map { it.first to it.third }, db.automatismoDao().todos(), fecha.toEpochDay(),
        )
        tocaCheck = claves.mapNotNull { c -> hacer.firstOrNull { it.first == c }?.let { c to it.second } }

        // Revisión semanal: cuenta desde el primer hábito de cualquier tipo (también los DEJAR).
        val inicios = hacer.map { it.third } + propios.filter { (p, _) -> p.tipo == TipoPropio.DEJAR }.map { (p, _) -> p.fechaInicio }
        val primero = inicios.minOrNull()
        tocaRev = primero != null &&
            tocaRevision(primero, db.revisionDao().todas().maxOfOrNull { it.semana }, fecha.toEpochDay())
    }

    fun cambiarVasos(delta: Int) {
        val actual = agua ?: return
        val nuevos = ajustarVasos(actual.vasos, delta)
        if (nuevos == actual.vasos) return
        scope.launch {
            // El primer registro crea el hábito Agua con inicio = hoy (no pisa el existente).
            db.registrarAgua(fecha.toEpochDay(), nuevos, actual.meta)
            agua = cargarAgua()
        }
    }

    fun elegirAnimo(valor: Int) {
        animo = valor
        scope.launch { db.animoDao().guardar(Animo(fecha.toEpochDay(), valor)) }
    }

    fun marcarChecklist(itemId: Long, hecho: Boolean) {
        val marca = ChecklistMarca(fecha.plusDays(1).toEpochDay(), itemId, hecho)
        paraManana = paraManana.filter { it.itemId != itemId } + marca
        scope.launch { db.checklistMarcaDao().guardar(marca) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        val diaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, LOCALE_ES)
            .replaceFirstChar { it.uppercase() }
        val mes = fecha.month.getDisplayName(TextStyle.SHORT, LOCALE_ES)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "$diaSemana ${fecha.dayOfMonth} $mes".uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Paleta.TextoSecundario,
                )
                Text("Hola ${perfil.nombre}", style = MaterialTheme.typography.titleLarge)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = alAbrirResumen) { Text("Resumen") }
            TextButton(onClick = alAbrirAjustes) { Text("Ajustes") }
            TextButton(onClick = alAbrirCopia) { Text("Copia") }
            TextButton(onClick = alNuevoHabito) { Text("Nuevo hábito") }
        }

        // Una invitación a la vez, para no llenar la pantalla; "Después" pasa a la siguiente o la oculta.
        val invitacion = tocaCheck.firstOrNull { it.first !in pospuestos.split(",") }
        if (invitacion != null) {
            TarjetaInvitacionAutomatismo(
                nombre = invitacion.second,
                alHacer = { alHacerCheck(invitacion.first) },
                alPosponer = { alPosponerCheck(invitacion.first) },
            )
        } else if (tocaRev && CLAVE_REVISION !in pospuestos.split(",")) {
            // La revisión espera a que no haya un check pendiente, para mostrar una sola invitación a la vez.
            TarjetaInvitacionRevision(
                alHacer = alAbrirRevision,
                alPosponer = { alPosponerCheck(CLAVE_REVISION) },
            )
        }

        if (cargado && propios.isEmpty()) {
            TarjetaRom(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Todavía no tenés hábitos", style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
                    Text(
                        "Empezá por uno solo, chico. Lo demás se agrega con el tiempo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Paleta.TextoSecundario,
                    )
                    BotonRom("Agregar mi primer hábito", modifier = Modifier.fillMaxWidth(), alTocar = alNuevoHabito)
                }
            }
        }

        if (perfil.aguaActiva) {
            TarjetaAgua(
                estado = agua,
                alAbrir = { alAbrir(TipoHabito.AGUA) },
                alCambiar = { cambiarVasos(it) },
            )
        }

        TarjetaAnimo(
            valor = animo,
            alAbrir = { alAbrir("ANIMO") },
            alElegir = { elegirAnimo(it) },
        )

        deslizDe?.let { d ->
            DialogoDesencadenante(
                alGuardar = { lugar, momento, emocion, compania ->
                    deslizDe = null
                    scope.launch { db.anotarDesencadenante(d.id, fecha.toEpochDay(), lugar, momento, emocion, compania) }
                },
                alCerrar = { deslizDe = null },
            )
        }

        propios.forEach { (p, estado) ->
            val dejar = p.tipo == TipoPropio.DEJAR
            TarjetaHabito(
                titulo = "${iconoDe(p)} ${p.nombre}",
                estado = estado,
                pregunta = if (dejar) "¿Cómo va el día?" else "¿Lo hiciste hoy?",
                // El 1.er botón siempre es el ✓: en DEJAR significa "no lo hice".
                textoSi = if (dejar) "No lo hice 💪" else "Sí ✓",
                textoNo = if (dejar) "Sí lo hice" else "Hoy no",
                mensajeSi = mensajeCelebracion(estado.racha, dejar),
                mensajeNo = textoNuncaDosVeces(estado.falloAyer, false).orEmpty(),
                mensajeAyer = textoNuncaDosVeces(estado.falloAyer, null),
                avisoFase = cruzoFase(estado.dia)?.let { textoAvisoFase(it) },
                alAbrir = { alAbrir(claveRegistroPropio(p.id)) },
                alElegir = { registrarPropio(p, it) },
                versionMinima = p.versionMinima,
                antojosHoy = if (dejar) antojosDeHoy(antojos, p.id, fecha.toEpochDay()) else null,
                alCambiarAntojo = { cambiarAntojo(p, it) },
            )
        }

        if (anoche.isNotEmpty()) {
            TarjetaRom(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Lo que preparaste anoche", style = MaterialTheme.typography.titleMedium)
                    itemsChecklist.forEach { FilaCheck(it.texto, estaHecho(anoche, it.id), null) }
                }
            }
        }

        TarjetaRom(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Para mañana (noche)", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { editandoChecklist = true }) { Text("Editar") }
                }
                itemsChecklist.forEach { item ->
                    FilaCheck(item.texto, estaHecho(paraManana, item.id)) { marcarChecklist(item.id, it) }
                }
            }
        }

        // Acceso fijo a la revisión semanal (R9) y a las anteriores, aunque no haya invitación.
        TextButton(onClick = alAbrirRevision, modifier = Modifier.fillMaxWidth()) { Text("Revisión semanal ›") }
    }

    if (editandoChecklist) {
        EditarChecklist(db) {
            editandoChecklist = false
            scope.launch {
                itemsChecklist = db.checklistItemDao().todos()
                anoche = db.checklistMarcaDao().deDia(fecha.toEpochDay())
                paraManana = db.checklistMarcaDao().deDia(fecha.plusDays(1).toEpochDay())
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
    mensajeAyer: String? = null,
    avisoFase: String? = null,
    versionMinima: String = "",
    antojosHoy: Int? = null,
    alCambiarAntojo: (Int) -> Unit = {},
) {
    // Tocar la tarjeta abre el Detalle (solo si el hábito ya empezó).
    TarjetaRom(alTocar = alAbrir, habilitada = estado != null, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (estado == null) {
                // Sin datos todavía.
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
            } else {
                val e = estadoDeFase(estado.dia)
                val nombreFase = when (e.fase) {
                    Fase.ADQUISICION -> "Adquisición"
                    Fase.CONSOLIDACION -> "Consolidación"
                    Fase.AUTOMATICO -> "Automático"
                }
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
                Text("$nombreFase · día ${estado.dia}", style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
                if (e.metaSiguiente != null) {
                    val avance = estado.dia.toFloat() / e.metaSiguiente
                    BarraRom(avance)
                    Text("${estado.dia}/${e.metaSiguiente}", style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
                } else {
                    Text("Automático 🎉", style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
                }
            }
            Text(pregunta, style = MaterialTheme.typography.bodyLarge)
            if (versionMinima.isNotBlank()) {
                Text("Versión mínima: $versionMinima", style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val elegido = estado?.hoy
                BotonOpcion(textoSi, elegido == true, Modifier.weight(1f)) { alElegir(true) }
                BotonOpcion(textoNo, elegido == false, Modifier.weight(1f)) { alElegir(false) }
            }
            when (estado?.hoy) {
                true -> Text(mensajeSi, style = MaterialTheme.typography.bodyMedium, color = Paleta.Verde)
                false -> Text(mensajeNo, style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
                // R13: ayer no se dio y hoy todavía no hay registro.
                null -> if (mensajeAyer != null) {
                    Text(mensajeAyer, style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
                }
            }
            // R15: aviso suave durante la semana del cambio de fase.
            if (avisoFase != null) {
                Text(avisoFase, style = MaterialTheme.typography.bodyMedium, color = Paleta.Titulo)
            }
            if (antojosHoy != null) {
                BloqueAntojo(antojosHoy, alSumar = { alCambiarAntojo(1) }, alRestar = { alCambiarAntojo(-1) })
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
private fun TarjetaAgua(estado: AguaEstado?, alAbrir: () -> Unit, alCambiar: (Int) -> Unit) {
    // Tocar la tarjeta abre el Detalle (solo si el hábito ya empezó).
    TarjetaRom(alTocar = alAbrir, habilitada = estado?.dia != null, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val dia = estado?.dia
            if (dia == null) {
                Text("AGUA", style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
            } else {
                val e = estadoDeFase(dia)
                val nombreFase = when (e.fase) {
                    Fase.ADQUISICION -> "Adquisición"
                    Fase.CONSOLIDACION -> "Consolidación"
                    Fase.AUTOMATICO -> "Automático"
                }
                Text("AGUA", style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
                Text("$nombreFase · día $dia", style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
                if (e.metaSiguiente != null) {
                    BarraRom(dia.toFloat() / e.metaSiguiente)
                    Text("$dia/${e.metaSiguiente}", style = MaterialTheme.typography.bodySmall, color = Paleta.TextoSecundario)
                } else {
                    Text("Automático 🎉", style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
                }
            }
            val vasos = estado?.vasos ?: 0
            val meta = estado?.meta ?: 0
            Text("¿Cuántos vasos llevás hoy?", style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { alCambiar(-1) },
                    enabled = estado != null && vasos > 0,
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
                    border = BorderStroke(1.dp, Paleta.Borde),
                ) { Text("−1", style = MaterialTheme.typography.titleMedium) }
                Text(
                    "$vasos / $meta",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.2f),
                )
                Button(
                    onClick = { alCambiar(1) },
                    enabled = estado != null,
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                ) { Text("+1", style = MaterialTheme.typography.titleMedium) }
            }
            if (estado != null && vasos > 0) {
                Text(
                    if (cumplidoAgua(vasos, meta)) "¡Meta cumplida!" else "Te faltan ${meta - vasos}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (cumplidoAgua(vasos, meta)) Paleta.Verde else Paleta.TextoSecundario,
                )
            }
            if (dia != null) {
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

/** Ánimo de hoy: 5 caritas, 1 toque; tocar otra lo corrige. */
@Composable
private fun TarjetaAnimo(valor: Int?, alAbrir: () -> Unit, alElegir: (Int) -> Unit) {
    TarjetaRom(alTocar = alAbrir, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("ÁNIMO", style = MaterialTheme.typography.titleMedium, color = Paleta.Titulo)
            Text("¿Cómo te sentís hoy?", style = MaterialTheme.typography.bodyLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CARITAS.forEachIndexed { i, carita ->
                    BotonOpcion(carita, elegido = valor == i + 1, modifier = Modifier.weight(1f)) { alElegir(i + 1) }
                }
            }
            if (valor != null) Text(NOMBRES_ANIMO[valor - 1], style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
            Text("Ver calendario ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun BotonOpcion(texto: String, elegido: Boolean, modifier: Modifier, alTocar: () -> Unit) {
    val m = modifier.heightIn(min = 52.dp)
    if (elegido) Button(onClick = alTocar, modifier = m, contentPadding = PaddingValues(horizontal = 8.dp)) { Text(texto) }
    else OutlinedButton(
        onClick = alTocar,
        modifier = m,
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Acento),
        border = BorderStroke(1.dp, Paleta.Borde),
    ) { Text(texto) }
}

/** [alCambiar] null = solo lectura. */
@Composable
private fun FilaCheck(texto: String, marcado: Boolean, alCambiar: ((Boolean) -> Unit)?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = marcado, onCheckedChange = alCambiar, enabled = alCambiar != null)
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}
