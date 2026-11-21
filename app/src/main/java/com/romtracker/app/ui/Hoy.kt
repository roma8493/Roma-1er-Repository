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
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Checklist
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
import com.romtracker.app.logic.cumplidoAgua
import com.romtracker.app.logic.diaDelHabito
import com.romtracker.app.logic.estadoDeFase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val LOCALE_ES = Locale("es", "AR")

/** Estado de un hábito para pintar su tarjeta. [hoy]: null = sin dato. */
private data class TarjetaEstado(val dia: Int, val hoy: Boolean?)

/** Estado del agua. [dia] null = el hábito todavía no empezó (empieza con el primer vaso). */
private data class AguaEstado(val dia: Int?, val vasos: Int, val meta: Int)

/**
 * Pantalla P2 (Hoy): tarjetas Gym y Comida con registro en 1 toque (tocar el otro botón corrige),
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
) {
    val fecha = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()

    var gym by remember { mutableStateOf<TarjetaEstado?>(null) }
    var comida by remember { mutableStateOf<TarjetaEstado?>(null) }
    var agua by remember { mutableStateOf<AguaEstado?>(null) }
    var animo by remember { mutableStateOf<Int?>(null) }
    // Hábitos propios con su estado de hoy (TarjetaEstado.hoy = ✓ ya resuelto según el tipo).
    var propios by remember { mutableStateOf<List<Pair<HabitoPropio, TarjetaEstado>>>(emptyList()) }
    var anoche by remember { mutableStateOf<Checklist?>(null) }
    var paraManana by remember { mutableStateOf(Checklist(fecha.plusDays(1).toEpochDay())) }

    suspend fun cargarHabito(tipo: String): TarjetaEstado? {
        val habito = db.habitoDao().obtener(tipo) ?: return null
        val registroHoy = db.registroDao().todos(tipo)
            .firstOrNull { it.fecha == fecha.toEpochDay() }?.cumplido
        return TarjetaEstado(diaDelHabito(LocalDate.ofEpochDay(habito.fechaInicio), fecha), registroHoy)
    }

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
            val registroHoy = db.registroDao().todos(claveRegistroPropio(p.id))
                .firstOrNull { it.fecha == fecha.toEpochDay() }?.cumplido
            p to TarjetaEstado(diaDelHabito(LocalDate.ofEpochDay(p.fechaInicio), fecha), registroHoy)
        }

    fun registrarPropio(p: HabitoPropio, cumplido: Boolean) {
        scope.launch {
            db.registroDao().guardar(Registro(claveRegistroPropio(p.id), fecha.toEpochDay(), cumplido))
            propios = cargarPropios()
        }
    }

    LaunchedEffect(Unit) {
        propios = cargarPropios()
        gym = cargarHabito(TipoHabito.GYM)
        agua = cargarAgua()
        comida = cargarHabito(TipoHabito.COMIDA)
        animo = db.animoDao().todos().firstOrNull { it.fecha == fecha.toEpochDay() }?.valor
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

    fun guardarChecklist(nueva: Checklist) {
        paraManana = nueva
        scope.launch { db.checklistDao().guardar(nueva) }
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
            TextButton(onClick = alAbrirAjustes) { Text("Ajustes") }
            TextButton(onClick = alAbrirCopia) { Text("Copia") }
            TextButton(onClick = alNuevoHabito) { Text("Nuevo hábito") }
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

        TarjetaAgua(
            estado = agua,
            alAbrir = { alAbrir(TipoHabito.AGUA) },
            alCambiar = { cambiarVasos(it) },
        )

        TarjetaAnimo(
            valor = animo,
            alAbrir = { alAbrir("ANIMO") },
            alElegir = { elegirAnimo(it) },
        )

        propios.forEach { (p, estado) ->
            val dejar = p.tipo == TipoPropio.DEJAR
            TarjetaHabito(
                titulo = p.nombre,
                estado = estado,
                pregunta = if (dejar) "¿Cómo va el día?" else "¿Lo hiciste hoy?",
                // El 1.er botón siempre es el ✓: en DEJAR significa "no lo hice".
                textoSi = if (dejar) "No lo hice 💪" else "Sí ✓",
                textoNo = if (dejar) "Sí lo hice" else "Hoy no",
                mensajeSi = if (dejar) "Un día más sin hacerlo. ¡Bien!" else "¡Qué bueno que lo hiciste!",
                mensajeNo = if (dejar) "Un día no borra lo que venís logrando." else "Mañana es otra oportunidad.",
                alAbrir = { alAbrir(claveRegistroPropio(p.id)) },
                alElegir = { registrarPropio(p, it) },
            )
        }

        if (anoche != null) {
            val a = anoche!!
            TarjetaRom(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Lo que preparaste anoche", style = MaterialTheme.typography.titleMedium)
                    FilaCheck("Bolso del gym listo", a.bolso, null)
                    FilaCheck("Comida preparada", a.comida, null)
                    FilaCheck("Decidí a qué hora voy", a.hora, null)
                }
            }
        }

        TarjetaRom(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Para mañana (noche)", style = MaterialTheme.typography.titleMedium)
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
    TarjetaRom(alTocar = alAbrir, habilitada = estado != null, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (estado == null) {
                // Solo la comida puede no existir todavía: empieza con el primer registro.
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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val elegido = estado?.hoy
                BotonOpcion(textoSi, elegido == true, Modifier.weight(1f)) { alElegir(true) }
                BotonOpcion(textoNo, elegido == false, Modifier.weight(1f)) { alElegir(false) }
            }
            when (estado?.hoy) {
                true -> Text(mensajeSi, style = MaterialTheme.typography.bodyMedium, color = Paleta.Verde)
                false -> Text(mensajeNo, style = MaterialTheme.typography.bodyMedium, color = Paleta.TextoSecundario)
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
