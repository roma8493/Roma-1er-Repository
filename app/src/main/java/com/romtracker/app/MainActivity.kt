package com.romtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Habito
import com.romtracker.app.data.Perfil
import com.romtracker.app.data.TipoHabito
import com.romtracker.app.ui.Bienvenida
import kotlinx.coroutines.launch
import java.time.LocalDate

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

                    LaunchedEffect(Unit) {
                        perfil = db.perfilDao().obtener()
                        cargando = false
                    }

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
                        else -> Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            // Provisorio hasta T7 (pantalla Hoy)
                            Text("RomTracker", style = MaterialTheme.typography.headlineLarge)
                            Text("Hola, ${perfil!!.nombre}.")
                            Text("Después de ${perfil!!.claveAncla}, voy al gym.")
                            Text("Por qué: ${perfil!!.porQue}")
                        }
                    }
                }
            }
        }
    }
}
