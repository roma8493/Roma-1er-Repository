package com.romtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Habito
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.TemaPerfil
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.ui.Ajustes
import com.romtracker.app.ui.Bienvenida
import com.romtracker.app.ui.Detalle
import com.romtracker.app.ui.DetalleAnimo
import com.romtracker.app.ui.CheckAutomatismo
import com.romtracker.app.ui.Hoy
import com.romtracker.app.ui.PREFIJO_AUTOMATISMO
import com.romtracker.app.ui.PantallaRevision
import com.romtracker.app.logic.nombresParaAncla
import com.romtracker.app.ui.NuevoHabito
import com.romtracker.app.ui.PantallaCopia
import com.romtracker.app.ui.Resumen
import com.romtracker.app.ui.TemaRomTracker
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val PANTALLA_COPIA = "COPIA"
private const val PANTALLA_AJUSTES = "AJUSTES"
private const val PANTALLA_ANIMO = "ANIMO"
private const val PANTALLA_NUEVO_HABITO = "NUEVO_HABITO"
private const val PANTALLA_RESUMEN = "RESUMEN"
private const val PANTALLA_REVISION = "REVISION"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.obtener(this)
        setContent {
            // El perfil se carga por encima del tema porque trae el tema elegido (Sistema/Claro/Oscuro).
            // cargando = true hasta saber si ya existe el perfil
            var cargando by remember { mutableStateOf(true) }
            var perfil by remember { mutableStateOf<Perfil?>(null) }
            val scope = rememberCoroutineScope()
            TemaRomTracker(perfil?.tema ?: TemaPerfil.CLARO) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // null = pantalla Hoy; si no, el tipo de hábito cuyo Detalle se ve
                    var abierto by rememberSaveable { mutableStateOf<String?>(null) }
                    // Claves de hábitos cuyo check de automatismo se dejó "para después" (separadas por coma).
                    var pospuestos by rememberSaveable { mutableStateOf("") }

                    var otrosHabitos by remember { mutableStateOf<List<String>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        perfil = db.perfilDao().obtener()
                        cargando = false
                    }
                    // R16: al abrir Nuevo hábito se ofrecen los hábitos existentes como ancla.
                    LaunchedEffect(abierto) {
                        if (abierto == PANTALLA_NUEVO_HABITO) otrosHabitos = nombresParaAncla(db.habitoPropioDao().todos())
                    }

                    // safeDrawingPadding: respeta barra de estado, cámara frontal y barra de gestos
                    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                    when {
                        cargando -> Unit
                        perfil == null -> Bienvenida { nuevo ->
                            scope.launch {
                                db.perfilDao().guardar(nuevo)
                                db.sembrarChecklistSiVacia()
                                perfil = nuevo
                            }
                        }
                        abierto == null -> Hoy(
                            db, perfil!!,
                            alAbrir = { abierto = it },
                            alAbrirCopia = { abierto = PANTALLA_COPIA },
                            alAbrirAjustes = { abierto = PANTALLA_AJUSTES },
                            alNuevoHabito = { abierto = PANTALLA_NUEVO_HABITO },
                            alAbrirResumen = { abierto = PANTALLA_RESUMEN },
                            alHacerCheck = { abierto = PREFIJO_AUTOMATISMO + it },
                            pospuestos = pospuestos,
                            alPosponerCheck = { pospuestos = if (pospuestos.isEmpty()) it else "$pospuestos,$it" },
                            alAbrirRevision = { abierto = PANTALLA_REVISION },
                        )
                        abierto!!.startsWith(PREFIJO_AUTOMATISMO) -> CheckAutomatismo(
                            db,
                            clave = abierto!!.removePrefix(PREFIJO_AUTOMATISMO),
                            alVolver = { abierto = null },
                        )
                        abierto == PANTALLA_REVISION -> PantallaRevision(db) { abierto = null }
                        abierto == PANTALLA_RESUMEN -> Resumen(
                            db,
                            alAbrir = { abierto = it },
                            alVolver = { abierto = null },
                        )
                        abierto == PANTALLA_NUEVO_HABITO -> NuevoHabito(
                            otrosHabitos = otrosHabitos,
                            alVolver = { abierto = null },
                            alCrear = { nombre, tipo, ancla, porQue, versionMinima, icono, plan ->
                                scope.launch {
                                    db.crearPropio(nombre, ancla, porQue, tipo, LocalDate.now().toEpochDay(), versionMinima, icono, plan)
                                    abierto = null
                                }
                            },
                        )
                        abierto == PANTALLA_AJUSTES -> Ajustes(
                            perfil!!,
                            alVolver = { abierto = null },
                            alGuardar = { nuevo ->
                                scope.launch {
                                    db.guardarPerfil(nuevo, LocalDate.now().toEpochDay())
                                    perfil = nuevo
                                    abierto = null
                                }
                            },
                            alCambiarTema = { tema ->
                                // Efecto inmediato: se guarda y se aplica sin salir de Ajustes
                                val actual = perfil
                                if (actual != null && actual.tema != tema) {
                                    val nuevo = actual.copy(tema = tema)
                                    scope.launch {
                                        db.guardarPerfil(nuevo, LocalDate.now().toEpochDay())
                                        perfil = nuevo
                                    }
                                }
                            },
                        )
                        abierto == PANTALLA_COPIA -> PantallaCopia(
                            db,
                            alVolver = { abierto = null },
                            alRestaurar = { restaurado -> perfil = restaurado; abierto = null },
                        )
                        abierto == PANTALLA_ANIMO -> DetalleAnimo(db) { abierto = null }
                        else -> Detalle(db, perfil!!, abierto!!) { abierto = null }
                    }
                    }
                }
            }
        }
    }
}
