# Bitácora de Progreso — RomTracker (Memoria entre Sesiones)

> Reemplaza a `PROJECT_PROGRESS.md` del Master Prompt. Actualizar al cerrar cada tarea/iteración.

## Ficha del Proyecto
- **Nombre**: RomTracker
- **Objetivo**: App Android (APK sin Play Store) de hábitos basada en ciencia, para usuarios que solo pueden actuar después del trabajo.
- **Tecnología**: Kotlin + Jetpack Compose + Room; compila GitHub Actions (ver Etapa 3 en `docs/SPEC.md`).
- **Versión actual**: 0.4.0 (versionCode 13; hábitos propios: crear + tarjetas + Detalle, sin editar/borrar todavía). Antes 0.2.5 (versionCode 12; cruce ánimo–hábitos). Versión 0.3 (agua + ánimo + cruce) CERRADA y confirmada por el usuario en el A34 (2026-11-20).
- **Estado**: Fases 1 y 2 completadas. Fase 3 completa (Etapas 1–3 aprobadas en `docs/SPEC.md`). Fase 4 completa: T4–T9 hechas. MVP 0.1.0 cerrado.

## Última Actualización
- **Fecha**: 2026-11-20
- **Resumen**: T17 implementada (pendiente Action y prueba en el A34; versión 0.4.0, versionCode 13): pantalla `ui/NuevoHabito.kt` (nombre, "Quiero hacerlo/dejarlo", ancla, por qué; botón "Nuevo hábito" en Hoy), tarjetas propias en Hoy (reutilizan `TarjetaHabito`; el 1.er botón es siempre el ✓: en DEJAR = "No lo hice 💪") y `Detalle` con rama `PROPIO_<id>` (calendario con opciones según tipo, ancla y por qué). Antes, T16 hecha (Action en verde, run 44773775663, commit 41b5698; sin UI todavía, el APK no cambia para el usuario): tabla `habito_propio` (id autogenerado, nombre, claveAncla, porQue, tipo HACER/DEJAR, fechaInicio) con `Migration(4,5)` (solo CREATE TABLE, Room v5), `HabitoPropioDao`, `AppDatabase.crearPropio/borrarPropio` (borra también sus registros en una transacción), `logic/Propios.kt` (`cumplidoPropio`, `hizoLaAccionPropio`, `registrosDePropio`), copia de seguridad versión 5 ("habitosPropios" opcional; las 1–4 se siguen leyendo), tests `PropiosTest` y `CopiaTest`. Antes, T15 implementada (pendiente Action y prueba en el A34): sección "Ánimo y hábitos" al final de `ui/DetalleAnimo.kt`; con <14 días de ánimo muestra cuántos faltan, con ≥14 compara promedio en días ✓ vs no ✓ de Gym, Comida y Agua (`logic/Animo.kt`: `cruceAnimo`, `diasFaltanParaCruce`, `textoCruce`; tests `CruceAnimoTest`). T14 confirmada por el usuario (2026-11-20):  tabla `animo` (fecha, valor 1–5) con `Migration(3,4)` (solo CREATE TABLE, Room v4), `AnimoDao`, tarjeta "ÁNIMO" en Hoy (5 caritas emoji, 1 toque, corrige), pantalla `ui/DetalleAnimo.kt` con calendario (editable hoy y 7 días atrás, menú de 5 caritas + Sin dato), copia de seguridad versión 4 ("animos" opcional; las 1–3 se siguen leyendo), `logic/Animo.kt`, tests `AnimoTest` y `CopiaTest`. T13 confirmada por el usuario (2026-11-20): T13 implementada (pendiente Action y prueba en el A34): tarjeta Agua en Hoy (−1 / +1, "n / meta", fase y barra), Detalle de AGUA con calendario (celda "vasos/meta", menú +1/−1/Sin dato, editable hoy y 7 días atrás), campo "Meta de vasos" en Ajustes. `Perfil.metaAgua` (default 8) con `Migration(2,3)` (ALTER TABLE ADD COLUMN); `AppDatabase.guardarPerfil` actualiza la meta de hoy si ya tiene vasos; copia de seguridad versión 3 (las 1 y 2 se siguen leyendo, meta 8 por defecto); se quitó `metaActual`. T12 confirmada por el usuario el 2026-11-20 (0.2.2 abre y guarda bien en el A34; la migración 1→2 no perdió datos). T12 implementada: tabla `agua` (fecha, vasos, meta del día) con `Migration(1,2)` que solo hace CREATE TABLE, `AguaDao`, `registrarAgua`, `logic/Agua.kt` (✓ por meta, reutiliza fase/consistencia/racha), copia de seguridad versión 2 (las copias 0.2 se siguen leyendo), tests `AguaTest` y `CopiaTest`. T11 hecha y confirmada por el usuario (`ui/Ajustes.kt`: editar nombre, hora, clave ancla y por qué; botón "Ajustes" en Hoy; Action en verde, run 44630374339, commit 5453b40). T10 hecha y confirmada en el A34 (copia de seguridad: `data/Copia.kt`, `ui/Copia.kt`, botón "Copia" en Hoy; Action en verde, run 44613033318, commit 7b46a3f). MVP 0.1.0 cerrado. T9 hecha: revisión de textos (sin "21 días" ni lenguaje culpabilizador), singular "Falta 1 día", versión 0.1.0 y `README.md` con instalación (Action en verde, run 44607954217, commit 880d943). T8 confirmada en el A34 (con ajustes: barra más gruesa/contraste y pista "Ver calendario y detalle ›" en las tarjetas de Hoy). T8 implementada (`ui/Detalle.kt`): fase, barra, consistencia, racha, calendario mensual (editables hoy y 7 días atrás, no antes del inicio del hábito; menú Fui/No fui/Sin dato) y clave ancla/por qué en Gym; tarjetas de Hoy abren el Detalle; `esEditable` con tests. T7 confirmada en el A34. Antes, T7 implementada (`ui/Hoy.kt`): tarjetas Gym/Comida con fase y barra, botones que registran en 1 toque y corrigen, checklist "para mañana" y "lo que preparaste anoche"; tocar tarjeta → Detalle queda para T8. T6 confirmada en el A34 (persiste tras reiniciar). Antes, T6 hecha: Bienvenida (nombre+hora de salida, clave ancla, por qué) en `ui/Bienvenida.kt`; al pulsar Empezar guarda Perfil y crea el hábito GYM (inicio = hoy) en Room; sin perfil la app siempre vuelve al paso 1. Pendiente confirmar Action y probar en el A34. Antes, T5 hecha: capa Room (Perfil, Habito, Registro, Checklist + DAOs) en `data/` y lógica pura de fase/consistencia/racha en `logic/`, con 13 tests. Action en verde (run 44446995312, commit 6655abc). T4 confirmada en el A34 (instala y abre).

## Funcionalidades
- **Implementadas (sin UI aún)**: base de datos local y lógica de fase/consistencia/racha.
- **Pendientes (MVP 0.1)**: perfil, gimnasio, comida sana/chatarra, fase cerebral + consistencia, checklist nocturna (ver `docs/SPEC.md`).

## Decisiones Clave y Reglas Aprendidas
- Mantener `CLAUDE.md` por debajo de 100 líneas.
- Ejecutar una sola tarea de `docs/SPEC.md` por cada sesión limpia (`/clear`).
- Única bitácora: este archivo (no crear `PROJECT_PROGRESS.md`).
- Mapeo de marcos: Fase 3 (SPEC) = Etapas 1–3 del Master Prompt (MVP, diseño, tecnología); Fase 4 = Etapas 4–5 (APK 0.1 + ciclos A–G).
- Modelo: Opus 5.5 para planificación (Fases 2–3); Sonnet 5.5 para ejecución (Fase 4), cuando Claude lo indique.
- Fuentes crudas (`MASTER PROMPT.md`, `INFORME_HABITOS.md`) no se releen; usar `docs/notas/`.
- MVP 0.1: datos solo en el celular (sin Internet/login); sin notificaciones; celular destino Samsung Galaxy A34 (mínimo Android 8).
- MVP 0.1 incluye gym, comida, checklist nocturna fija (3 ítems) y reglas R1, R4, R5, R7, R8. Agua y demás reglas → versiones futuras.
- Días sin registrar = "sin dato"; se pueden completar hasta 7 días atrás.
- Diseño 0.1: 3 pantallas (Bienvenida 3 pasos → Hoy ⇄ Detalle con calendario mensual). Sin ajustes en 0.1.
- Checklist ítem 3 = casilla "Decidí a qué hora voy" (sin elegir hora). Comida también tiene fase cerebral.
- Este entorno no tiene JDK/SDK y tiene 3 GB RAM: NO compilar acá; compila GitHub Actions en cada push a `main`.
- El usuario autorizó commit y push directos sin preguntar (2026-11-19).
- No hay `gh` en este entorno; estado de Actions se consulta con la API pública (curl).
- Definiciones T5: día 1 = día de inicio; día 90 y 270 ya son fase nueva. Consistencia X de Y: Y = días desde inicio, X = días ✓ (sin dato no es ✓). Racha: ✓ seguidos hasta hoy; hoy sin registro no la corta; no fui y sin dato la cortan; nunca afecta la fase. Fechas en Room como epochDay; sin fila = sin dato.
- Inicio del hábito: gym = fecha de configuración; comida = primer registro (se crea con `HabitoDao.crear`, no pisa el existente).
- Clave debug fija en el repo para que los APK nuevos se instalen encima sin perder datos.

- Plan 0.3 aprobado 2026-11-19: agua = hábito con fase, vasos con meta 8 editable; ánimo = 5 caritas; cruce ánimo–hábitos con umbral de 14 días. Tareas T12–T15 en `docs/SPEC.md`.
- T15: el cruce solo usa días con ánimo Y con registro del hábito (sin dato no entra); umbral 14 días de ánimo en total; diferencia ≥0,5 caritas = "más alto", si no "sin diferencia clara"; texto "se asocia", nunca "causa". Sin migración ni cambio en la copia.
- T14: el ánimo NO es un hábito (sin fila en `habito`, sin fase ni racha); editable hoy y 7 días atrás (`animoEditable`, sin fecha de inicio). Caritas = emojis de texto 😞🙁😐🙂😄. Un valor por día, sin notas.
- T13: la meta de agua vive en `Perfil.metaAgua` (única fuente; sin filas fantasma). Día nuevo = meta del perfil; cada fila de `agua` conserva su meta. Cambiar la meta en Ajustes rige desde hoy (actualiza la fila de hoy si existe). Reemplaza la regla de T12 "meta vigente = último día registrado".
- T12: el agua va en tabla propia `agua` (cada día guarda su meta; cambiar la meta no reinterpreta el pasado). Meta vigente = la del último día registrado, 8 por defecto. Inicio del hábito Agua = primer registro (`registrarAgua`).

- T16: los días de un hábito propio van en la tabla `registro` con `habito = "PROPIO_<id>"` y `cumplido` = ✓ ya resuelto (en DEJAR, ✓ = ese día no lo hice; la UI usa `cumplidoPropio`/`hizoLaAccionPropio`). Inicio = fecha de creación (`fechaInicio`). No se usa la tabla `habito`. Versión de la app sin cambiar hasta T17.

- T17: en los hábitos propios el registro guarda el ✓ ya resuelto; Hoy y Detalle no convierten nada (el 1.er botón/opción siempre es ✓). Sin límite de hábitos propios. Editar/borrar = T18.

## Problemas Conocidos
- Corregido en T7: el contenido se dibujaba bajo la barra de estado/cámara frontal (Android 15 fuerza pantalla completa); se agregó `safeDrawingPadding()` en `MainActivity.kt`. Falta confirmar en el A34.

## Últimos Cambios
- Creados `.claude/settings.json`, `CLAUDE.md`, `docs/index.md`, `docs/PROGRESS.md`.
- Creadas 3 notas en `docs/notas/` (producto, ciencia-habitos, reglas-app-desde-ciencia), registradas en `docs/index.md`.

## Cómo Compilar e Instalar
- Ver `README.md` (descarga del artifact desde GitHub Actions e instalación encima del anterior).

## Checklist de Tareas Activas
- [x] Fase 1: Cimientos (settings, CLAUDE.md, index, PROGRESS).
- [x] Fase 2: Crear `docs/notas/producto.md`.
- [x] Fase 2: Crear `docs/notas/ciencia-habitos.md`.
- [x] Fase 2: Crear `docs/notas/reglas-app-desde-ciencia.md` (reglas R1–R12).
- [x] Fase 3 / Etapa 1: MVP 0.1 en `docs/SPEC.md` (aprobado 2026-11-19).
- [x] Fase 3 / Etapa 2: diseño y prototipo (pantallas y flujo de R-01 a R-06).
- [x] Fase 3 / Etapa 3: tecnología y comando de compilación definitivo.
- [x] Fase 4: tareas T4–T9 de `docs/SPEC.md` (APK 0.1) y luego ciclos iterativos (Etapa 5).

## Próximo Paso
- Confirmar Action de T17 y probar en el A34 (ver "Qué probar"); luego marcar T17 `[x]` en `docs/SPEC.md`. Siguiente: T18 (editar y borrar hábito propio), en sesión limpia (`/clear`).
