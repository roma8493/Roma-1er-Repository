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
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.ui.Ajustes
import com.romtracker.app.ui.Bienvenida
import com.romtracker.app.ui.Detalle
import com.romtracker.app.ui.DetalleAnimo
import com.romtracker.app.ui.Hoy
import com.romtracker.app.ui.NuevoHabito
import com.romtracker.app.ui.PantallaCopia
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val PANTALLA_COPIA = "COPIA"
private const val PANTALLA_AJUSTES = "AJUSTES"
private const val PANTALLA_ANIMO = "ANIMO"
private const val PANTALLA_NUEVO_HABITO = "NUEVO_HABITO"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.obtener(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // cargando = true hasta saber si ya existe el perfil
                    var cargando by remember { mutableStateOf(true) }
                    var perfil by remember { mutableStateOf<Perfil?>(null) }
                    val scope = rememberCoroutineScope()
                    // null = pantalla Hoy; si no, el tipo de hábito cuyo Detalle se ve
                    var abierto by rememberSaveable { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        perfil = db.perfilDao().obtener()
                        cargando = false
                    }

                    // safeDrawingPadding: respeta barra de estado, cámara frontal y barra de gestos
                    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                    when {
                        cargando -> Unit
                        perfil == null -> Bienvenida { nuevo ->
                            scope.launch {
                                db.perfilDao().guardar(nuevo)
                                // El gym empieza el día de la configuración (SPEC)
                                db.habitoDao().crear(
                                    Habito(TipoHabito.GYM, LocalDate.now().toEpochDay())
                                )
                                perfil = nuevo
                            }
                        }
                        abierto == null -> Hoy(
                            db, perfil!!,
                            alAbrir = { abierto = it },
                            alAbrirCopia = { abierto = PANTALLA_COPIA },
                            alAbrirAjustes = { abierto = PANTALLA_AJUSTES },
                            alNuevoHabito = { abierto = PANTALLA_NUEVO_HABITO },
                        )
                        abierto == PANTALLA_NUEVO_HABITO -> NuevoHabito(
                            alVolver = { abierto = null },
                            alCrear = { nombre, tipo, ancla, porQue ->
                                scope.launch {
                                    db.crearPropio(nombre, ancla, porQue, tipo, LocalDate.now().toEpochDay())
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
