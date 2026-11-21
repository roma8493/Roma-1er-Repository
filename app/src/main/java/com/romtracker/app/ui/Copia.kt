package com.romtracker.app.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.romtracker.app.data.AppDatabase
import com.romtracker.app.data.Copia
import com.romtracker.app.data.CopiaInvalida
import com.romtracker.app.data.CopiaJson
import com.romtracker.app.data.Perfil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/** Tope de lectura: una copia real pesa pocos KB; así un archivo enorme se rechaza sin cargarlo. */
private const val MAX_BYTES = 5_000_000

/** Pantalla de copia de seguridad: guardar y restaurar un archivo .json elegido por el usuario. */
@Composable
fun PantallaCopia(db: AppDatabase, alVolver: () -> Unit, alRestaurar: (Perfil) -> Unit) {
    BackHandler(onBack = alVolver)

    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf<String?>(null) }
    var pendiente by remember { mutableStateOf<Copia?>(null) } // copia leída, esperando confirmación

    val guardar = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            mensaje = try {
                val copia = db.exportar()
                if (copia == null) {
                    "Todavía no hay nada para guardar."
                } else {
                    withContext(Dispatchers.IO) {
                        val salida = contexto.contentResolver.openOutputStream(uri, "wt")
                            ?: throw IllegalStateException("sin salida")
                        salida.use { it.write(CopiaJson.aTexto(copia).toByteArray(Charsets.UTF_8)) }
                    }
                    "Copia guardada. Conservá ese archivo en un lugar seguro (Drive, mail, otra carpeta)."
                }
            } catch (e: Exception) {
                "No pude guardar la copia. Probá elegir otra carpeta."
            }
        }
    }

    val elegir = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val texto = withContext(Dispatchers.IO) {
                    val entrada = contexto.contentResolver.openInputStream(uri)
                        ?: throw CopiaInvalida("No pude abrir el archivo.")
                    entrada.use {
                        // Lectura manual con tope (readNBytes no existe antes de Android 13)
                        val acumulado = java.io.ByteArrayOutputStream()
                        val buffer = ByteArray(8192)
                        while (true) {
                            val n = it.read(buffer)
                            if (n < 0) break
                            if (acumulado.size() + n > MAX_BYTES) {
                                throw CopiaInvalida("Este archivo es demasiado grande para ser una copia de RomTracker.")
                            }
                            acumulado.write(buffer, 0, n)
                        }
                        String(acumulado.toByteArray(), Charsets.UTF_8)
                    }
                }
                pendiente = CopiaJson.deTexto(texto)
                mensaje = null
            } catch (e: CopiaInvalida) {
                mensaje = e.message
            } catch (e: Exception) {
                mensaje = "No pude leer el archivo."
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = alVolver) { Text("← Copia de seguridad", style = MaterialTheme.typography.titleLarge, color = Paleta.Texto) }

        TarjetaRom(Modifier.fillMaxWidth()) {
            Text(
                "Tus datos viven solo en este celular. Si desinstalás la app se borran, " +
                    "así que guardá una copia de vez en cuando.",
                modifier = Modifier.padding(20.dp),
                color = Paleta.TextoSecundario,
            )
        }

        BotonRom("Guardar copia", Modifier.fillMaxWidth()) {
            guardar.launch("romtracker-copia-${LocalDate.now()}.json")
        }

        BotonRom("Restaurar copia", Modifier.fillMaxWidth(), destacado = false) {
            elegir.launch(arrayOf("*/*"))
        }

        mensaje?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
    }

    pendiente?.let { copia ->
        AlertDialog(
            onDismissRequest = { pendiente = null },
            title = { Text("¿Restaurar esta copia?") },
            text = {
                Text(
                    "Es la copia de ${copia.perfil.nombre}, con ${copia.registros.size} registros. " +
                        "Esto reemplaza todo lo que hay ahora en la app."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    pendiente = null
                    scope.launch {
                        try {
                            val guardado = db.reemplazarTodo(copia)
                            alRestaurar(guardado)
                        } catch (e: Exception) {
                            mensaje = "No pude restaurar la copia. Tus datos no cambiaron."
                        }
                    }
                }) { Text("Restaurar") }
            },
            dismissButton = { TextButton(onClick = { pendiente = null }) { Text("Cancelar") } },
        )
    }
}
