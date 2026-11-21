# Especificación Activa — RomTracker

## Objetivo
- APK 0.1 instalable sin Play Store para que una persona agotada post-trabajo registre su gimnasio
  y su alimentación, y vea su avance según las fases reales del cerebro (no "21 días").

## Etapa 1 — MVP 0.1 (aprobada 2026-11-19)
### Requerimientos
- R-01 Perfil mínimo: nombre y hora de salida del trabajo. Datos guardados solo en el celular.
- R-02 Hábito Gimnasio: configurar clave ancla ("Después de ___, voy al gym")
  y "¿Por qué es importante para vos?". Registrar cada día: fui / no fui.
- R-03 Hábito Comida: registrar cada día: día sano / día con chatarra-dulces.
- R-04 Progreso por hábito: fase cerebral (Adquisición 0–90, Consolidación 90–270, Automático 270+ días)
  con barra hacia la siguiente fase, y consistencia ("X de Y días"). Racha secundaria.
- R-05 Un desliz no reinicia a cero ni culpa: la fase avanza por días desde el inicio; la consistencia refleja los deslices.
- R-06 Checklist de la noche anterior: 3 ítems fijos (bolso del gym listo, comida preparada, hora de ir al gym)
  para el día siguiente; se marca a mano. Sin agenda, sin avisos.
- R-07 Textos como invitación ("¿Vamos?"), nunca "¡No cumpliste!" ni "debés". Prohibido "21 días".
- R-08 Funciona sin Internet. Compatible con Android 8+; probado en Samsung Galaxy A34.

### Fuera de alcance 0.1
- Login, nube, notificaciones/recordatorios, agua, desencadenantes y antojo resistido,
  check de automatismo, revisión semanal, hábitos personalizados, ítems editables de la checklist.

### Casos borde
- Día sin registrar: queda "sin dato" (no cuenta como fallo ni como éxito) y puede completarse después (hasta 7 días atrás).
- Cambio de opinión: un registro del día se puede corregir.
- Primer uso sin perfil: la app pide el perfil antes de mostrar hábitos.
- Cambio de fecha/hora del celular o medianoche: el "día" usa la fecha local del celular.
- Desinstalar la app borra los datos (aceptado en 0.1; la copia de seguridad llega después).
- Fase cerebral exacta en los límites (día 90 y 270).

### Criterios de aceptación
- Se instala el APK en el Galaxy A34 sin Play Store y abre sin errores.
- Se crea el perfil y la configuración del gym; persisten tras cerrar y reabrir la app y tras reiniciar el celular.
- Registrar gym y comida de hoy y de días anteriores actualiza fase, barra y consistencia correctamente
  (verificado con datos ficticios, incluyendo días 89/90/91 y 269/270/271).
- Un "no fui" no reinicia la fase.
- La checklist de la noche se marca y se ve al día siguiente.
- Ningún texto contiene lenguaje culpabilizador ni "21 días".

## Etapa 2 — Diseño y prototipo (aprobada 2026-11-19)
### Navegación
- P1 Bienvenida → P2 Hoy ⇄ P3 Detalle del hábito. Sin menús ni pantalla de ajustes en la 0.1.

### P1 · Bienvenida (solo primer uso, 3 pasos con "Siguiente") → R-01, R-02
1. "¿Cómo te llamás?" + "¿A qué hora salís del trabajo?" (selector de hora).
2. Clave ancla: "Después de ______, voy al gym" (sugerencia: "salir del trabajo").
3. "¿Por qué es importante para vos?" → "Empezar" → P2.
- Si se cierra a mitad de camino, la app vuelve a P1 (no hay hábitos sin perfil).

### P2 · Hoy (pantalla principal) → R-02, R-03, R-04 (resumen), R-06, R-07
```
┌─────────────────────────┐
│ Hola Roma · Jue 19 nov  │
├─────────────────────────┤
│ GYM  Adquisición día 34 │  ← tocar la tarjeta abre P3
│ ▓▓▓▓░░░░░░ 34/90        │
│ ¿Vamos hoy?             │
│ [ Fui ✓ ]  [ Hoy no ]   │
├─────────────────────────┤
│ COMIDA  Adquisición d.34│  ← tocar la tarjeta abre P3
│ ▓▓▓▓░░░░░░ 34/90        │
│ [ Sano ]  [ Chatarra ]  │
├─────────────────────────┤
│ Para mañana (noche):    │
│ ☐ Bolso del gym listo   │
│ ☐ Comida preparada      │
│ ☐ Decidí a qué hora voy │
└─────────────────────────┘
```
- El botón elegido queda resaltado; tocar el otro corrige el registro del día.
- "Hoy no" muestra un mensaje amable (ej. "Mañana es otra oportunidad"), sin culpa.
- La checklist marcada a la noche es "para mañana": al día siguiente se ve arriba como
  "Lo que preparaste anoche" (ya marcada) y abajo aparece una checklist nueva vacía.
- Ítem 3 es solo una casilla (no se elige hora, para no convertirlo en agenda).

### P3 · Detalle del hábito (misma pantalla para Gym y Comida) → R-04, R-05
```
┌─────────────────────────┐
│ ← GYM                   │
│ Fase: Adquisición d.34  │
│ ▓▓▓▓░░░░░░ faltan 56    │
│  días para Consolidación│
│ Consistencia: 24 de 34  │
│ Racha actual: 3 días    │
├─────────────────────────┤
│   ‹  Noviembre 2026  ›  │
│ L  M  M  J  V  S  D     │
│ ✓  ✗  ✓  ·  ✓  ✓  ·     │  ✓ fui/sano · ✗ no fui/chatarra
│ ...                     │  · sin dato
├─────────────────────────┤
│ "Después de salir del   │  (solo en Gym)
│  trabajo, voy al gym"   │
│ ¿Por qué? ...           │
└─────────────────────────┘
```
- Calendario mensual con colores. Editables: hoy y los 7 días anteriores; los más viejos se ven
  atenuados/bloqueados; los futuros, vacíos.
- Tocar un día editable abre un menú: "Fui / No fui / Sin dato" (o "Sano / Chatarra / Sin dato").
- Fases: Adquisición 0–90, Consolidación 90–270, Automático 270+ (sin barra: "Automático 🎉").
  Los días 90 y 270 ya pertenecen a la fase nueva.
- Comida también muestra fase cerebral (el hábito "comer sano" avanza igual que el gym).

### Reglas de diseño
- Uso con una mano, botones grandes, a lo sumo 2 toques para registrar el día.
- Textos según R-07. Tema claro y simple con colores propios (sin copiar HabitKit/HabitBull).
- Nombre, hora y clave ancla no se editan después de la bienvenida en la 0.1 (ajustes → 0.2, T11).

## Etapa 3 — Tecnología (aprobada 2026-11-19)
- Lenguaje/UI: Kotlin + Jetpack Compose (Material 3), una sola Activity. Proyecto Gradle (Kotlin DSL).
- Almacenamiento local: Room (SQLite) con KSP. Sin Internet, sin permisos especiales.
- SDK: minSdk 26 (Android 8), targetSdk/compileSdk = último estable al crear el proyecto.
- Compilación: GitHub Actions (gratis). Cada push a `main` corre tests y compila; el APK queda como
  "artifact" descargable desde GitHub. No se compila en este entorno (sin JDK/SDK, 3 GB RAM).
- Comandos (los corre GitHub Actions): `./gradlew testDebugUnitTest assembleDebug`.
  Salida: `app/build/outputs/apk/debug/app-debug.apk`.
- APK 0.1: debug. Se incluye en el repo una clave debug FIJA (`app/debug.keystore`, no es secreta)
  para que cada APK nuevo se instale encima del anterior sin desinstalar ni perder datos.
- Versión: `versionName` 0.1.x y `versionCode` creciente en cada APK entregado.
- Lógica de fases y consistencia en funciones puras con tests unitarios (sin celular).

## Tareas Atómicas
- [x] T1: Redactar y aprobar Etapa 1 (MVP 0.1).
- [x] T2: Etapa 2 — pantallas y flujo (bocetos) de R-01 a R-06.
- [x] T3: Etapa 3 — elegir tecnología y comando de compilación definitivo.
- [x] T4: Esqueleto del proyecto + clave debug fija + workflow de GitHub Actions. Verifica: Action en verde,
  APK descargable que se instala en el A34 y muestra una pantalla de bienvenida provisoria.
- [x] T5: Capa de datos (Room: perfil, hábitos, registros diarios, checklist) + lógica de fase/consistencia/racha.
  Verifica: tests unitarios pasan en Actions (días 89/90/91, 269/270/271, "no fui" no reinicia, "sin dato").
- [x] T6: Pantalla Bienvenida (3 pasos: perfil, clave ancla y "por qué"). Verifica: persiste tras cerrar/reiniciar.
- [x] T7: Pantalla Hoy (registro gym/comida en ≤2 toques, corregir, checklist nocturna). Verifica en el celular.
- [x] T8: Pantalla Detalle (fase, barra, consistencia, racha, calendario mensual, completar hasta 7 días atrás).
- [x] T9: Revisión de textos (R-07), APK 0.1.0 final, `README.md` con instalación. Verifica: criterios de aceptación.

## Versión 0.2 — Copia de seguridad (aprobada 2026-11-19)
- Icono "Copia" arriba a la derecha en Hoy → pantalla con "Guardar copia" y "Restaurar copia".
- Guardar: archivo `romtracker-copia-AAAA-MM-DD.json` que el usuario elige dónde guardar (selector de Android; sin permisos ni Internet). Incluye perfil, hábitos, registros y checklists, con número de versión.
- Restaurar: elige un archivo, valida, pide confirmación y reemplaza todo en una sola transacción. Archivo inválido → mensaje claro y no cambia nada.
- Criterios: (1) la copia se abre con un editor de texto; (2) desinstalar, instalar y restaurar deja nombre, ancla, fase y calendario como estaban; (3) un archivo cualquiera (ej. foto) se rechaza sin tocar los datos.

### Tareas 0.2
- [x] T10: Copia de seguridad (guardar/restaurar .json). Verifica en el celular: guardar, desinstalar, reinstalar, restaurar.
- [x] T11: Ajustes (editar nombre, hora de salida, clave ancla y "por qué"; botón "Ajustes" en Hoy). No toca registros ni fase. Verifica en el celular: cambiar nombre y ancla, ver el cambio en Hoy y Detalle, persiste al reabrir.

## Versión 0.3 — Agua y estado de ánimo (aprobada 2026-11-19)
- **Agua (hábito con fase)**: tarjeta en Hoy con botones +1 / −1 vaso y meta diaria (por defecto 8, editable en Ajustes). Día ✓ = vasos ≥ meta; menos vasos = "no fui"; sin registro = "sin dato". Misma fase cerebral (0–90, 90–270, 270+), barra, consistencia y racha que Gym y Comida. Inicio = primer registro. Detalle con calendario (editable hoy y 7 días atrás) y cantidad de vasos por día.
- **Estado de ánimo**: 5 caritas (muy mal → muy bien) en Hoy, 1 toque, se puede cambiar durante el día. Se ve en el calendario del Detalle. Sin notas de texto.
- **Cruce con hábitos**: pantalla/sección "Ánimo y hábitos" que compara el ánimo promedio en días ✓ vs. días no ✓ de cada hábito. Solo aparece con ≥14 días con ánimo registrado; antes muestra cuántos días faltan. Texto neutro ("se asocia", no "causa"), sin lenguaje culpabilizador.
- Copia de seguridad (T10) debe incluir agua y ánimo; los backups viejos siguen restaurándose.
- Criterios: (1) agua llega a la meta → día ✓ y suma a racha; (2) ánimo se guarda y se ve en el calendario tras reiniciar; (3) instalar encima de 0.2.1 no pierde datos (migración Room); (4) restaurar una copia 0.2 funciona.

### Tareas 0.3
- [x] T12: Agua — datos y lógica (Room con migración, DAO, regla de ✓ por meta, fase/consistencia/racha reutilizadas, copia de seguridad) con tests. Verifica: Action en verde.
- [x] T13: Agua — UI (tarjeta en Hoy con +1/−1, Detalle con calendario y vasos, meta en Ajustes). Verifica en el celular.
- [x] T14: Ánimo — datos y UI (5 caritas en Hoy, mostrar en calendario, copia de seguridad). Verifica en el celular.
- [x] T15: Cruce ánimo–hábitos (promedio ✓ vs no ✓, umbral de 14 días, textos neutros). Verifica en el celular con datos ficticios.

## Versión 0.4 — Hábitos propios (aprobada 2026-11-20)
- **Crear tarjetas de hábito** desde la app (botón "Nuevo hábito" en Hoy). Solo tipo Sí/No por día (sin contador).
- **Dos tipos al crear**: "Quiero hacerlo" (✓ = lo hice) y "Quiero dejarlo" (✓ = ese día no lo hice). La fase cerebral (0–90, 90–270, 270+), barra, consistencia y racha son las mismas que Gym y Comida.
- **Datos al crear**: nombre, clave ancla ("cuando pase X") y "por qué". Inicio del hábito = fecha de creación.
- **Tarjeta y Detalle**: igual que Gym (1 toque en Hoy, calendario editable hoy y 7 días atrás, ancla y por qué).
- **Gestión**: editar nombre, ancla y por qué; borrar definitivamente (con confirmación y aviso de que se pierde el historial). Sin archivar.
- Gym, Comida y Agua siguen como están (no se tocan ni se convierten).
- Copia de seguridad (versión 5) incluye los hábitos propios y sus registros; las copias 1–4 se siguen leyendo.
- Criterios: (1) crear un hábito aparece como tarjeta en Hoy y persiste al reiniciar; (2) en "Quiero dejarlo" el botón de ✓ significa "no lo hice" y la racha cuenta esos días; (3) editar y borrar funcionan y borrar pide confirmación; (4) instalar encima de 0.3 no pierde datos (migración Room 4→5); (5) restaurar una copia 0.3 funciona.

### Tareas 0.4
- [x] T16: Datos y lógica — habilitar hábitos propios en Room (migración 4→5, nombre/ancla/por qué/tipo), registro Sí/No para hábitos propios, regla de ✓ según tipo, reutilizar fase/consistencia/racha, copia de seguridad v5, con tests. Verifica: Action en verde.
- [x] T17: UI — pantalla "Nuevo hábito" (nombre, tipo, ancla, por qué), tarjetas propias en Hoy y Detalle con calendario. Verifica en el celular.
- [x] T18: Gestión — editar y borrar (con confirmación) un hábito propio. Verifica en el celular.

## Versión 0.5 — Rediseño estético premium (aprobada 2026-11-21)
- **Solo estética**: la lógica, los datos, la base de datos y la copia de seguridad NO cambian (sin migración Room). Sin funciones nuevas.
- **Estilo**: sobrio, inspirado en las apps de iPhone (tipo Apple Salud), implementado nativo en Android con Jetpack Compose. Nada específico de iOS.
- **Tema**: solo claro (no sigue el modo oscuro del celular). Fondo gris muy claro, tarjetas blancas con esquinas muy redondeadas (~20 dp) y sombra suave, mucho aire entre elementos.
- **Colores**: neutros grises + un único color de acento azul sobrio; verde/ámbar/rojo suave solo para ✓/estado. Contraste de texto alto (el usuario está agotado y usa el celular de noche).
- **Tipografía**: fuente del sistema (sin dependencias nuevas), jerarquía clara: título grande en negrita, subtítulos medianos, texto secundario en gris; números grandes en las tarjetas.
- **Botones y controles**: botones grandes y redondeados (alto ≥ 48 dp), barra de progreso fina y redondeada, sin bordes duros.
- **Look estático**: sin animaciones nuevas.
- **Orden de pantallas**: (1) tema central + Hoy; (2) Detalle de hábito y Detalle de ánimo; (3) Bienvenida, Nuevo hábito, Ajustes y Copia.
- **Fuera de alcance**: modo oscuro, animaciones, íconos personalizados, fuentes descargadas, cambiar textos o flujos.
- Criterios: (1) todas las pantallas usan el mismo tema (colores, esquinas, tipografía) desde un único archivo; (2) nada se corta ni se solapa en el A34 (incluida la barra de estado); (3) todo funciona igual que en 0.4.1 (registrar, editar, borrar, copia); (4) instalar encima de 0.4.1 no pierde datos; (5) el usuario confirma que "se ve premium".

### Tareas 0.5
- [x] T19: Tema central (`ui/Tema.kt`: colores, formas, tipografía, fondo claro) aplicado en `MainActivity.kt` + rediseño de Hoy (tarjetas Gym/Comida/Agua/Ánimo/propios y checklist). Versión 0.5.0. Verifica en el celular.
- [x] T20: Rediseño de Detalle de hábito (calendario, barra de fase, estadísticas) y Detalle de ánimo. Versión 0.5.1. Verifica en el celular.
- [x] T21: Rediseño de Bienvenida, Nuevo hábito, Ajustes y Copia + diálogos. Versión 0.5.2. Verifica en el celular.

## Versión 0.6 — Resumen global (pedido del usuario 2026-11-21)
- [x] T22: Pantalla "Resumen" (`ui/Resumen.kt`, botón en Hoy): cuadro con todos los hábitos (Gym, Comida, Agua, propios, Ánimo) × últimos 5 días, ✓/✕/vacío, navegación ‹ Antes / Después › de a 5 días, tocar un nombre abre su Detalle. Solo lectura, sin migración. Versión 0.6.0. Verifica en el celular.

## Versión 0.7 — Resumen de 7 días y checklist editable (pedido del usuario 2026-11-21)
Decisiones aprobadas: checklist con lista libre (agregar, renombrar, borrar), máximo 6 ítems y mínimo 1; los cambios rigen para todas las noches; "Lo que preparaste anoche" muestra solo los ítems actuales (borrar un ítem borra también sus marcas); Resumen con 7 columnas y navegación de a 7 días.
Datos: tabla nueva `checklist_item` (id autogenerado, texto, orden) y `checklist_marca` (paraFecha, itemId, hecho) con `Migration(5,6)` (Room v6): crea las tablas, siembra los 3 ítems actuales ("Bolso del gym listo", "Comida preparada", "Decidí a qué hora voy") y copia las marcas de la tabla `checklist` vieja, que se conserva sin tocar. Copia de seguridad versión 6 (las 1–5 se siguen leyendo).

- [x] T23 (agrupada con T24 y T25 en un solo APK 0.7.1, por pedido del usuario 2026-11-21): Resumen con 7 días: `DIAS_VISIBLES = 5` → 7 en `ui/Resumen.kt`, ‹ Antes / Después › avanza de a 7; las 7 columnas deben entrar en el ancho del A34 sin scroll horizontal (achicar columna del nombre si hace falta). Sin migración. Versión 0.6.1. Verifica en el celular.
- [x] T24: Datos de la checklist editable: tablas `checklist_item` y `checklist_marca`, `Migration(5,6)` con siembra y copia de marcas, DAOs, lógica pura (agregar, renombrar, borrar, tope 6, mínimo 1), copia de seguridad v6, tests (`ChecklistTest`, `CopiaTest`). Sin UI todavía: el APK no cambia para el usuario. Versión 0.7.0 (sin cambio visible).
- [x] T25: Hoy usa los ítems dinámicos: la tarjeta "Para mañana (noche)" y "Lo que preparaste anoche" leen `checklist_item`/`checklist_marca` en vez de las 3 casillas fijas. Se ve igual que antes con los 3 ítems por defecto. Versión 0.7.1. Verifica en el celular (instalar encima de 0.6.x: no se pierde nada y las marcas viejas siguen).
- [x] T26: Edición en la tarjeta "Para mañana (noche)": botón Editar → diálogo con la lista de ítems (renombrar, borrar con confirmación, agregar; "Agregar" deshabilitado con 6; no se puede borrar el último). Versión 0.7.2. Verifica en el celular.

Criterios de aceptación 0.7: (1) Resumen muestra 7 días y navega de a 7; (2) tras instalar encima de 0.6 los 3 ítems originales y sus marcas siguen; (3) se puede renombrar, agregar hasta 6 y borrar hasta 1; (4) borrar un ítem lo quita también de lo de anoche; (5) copia de seguridad hecha en 0.7 se restaura y una copia 0.6 sigue restaurando.

## Versión 0.8 — Modo oscuro, versión mínima (R10) y check de automatismo (R6) (aprobada por el usuario 2026-11-21)
Decisiones aprobadas:
- **Tema**: tres opciones en Ajustes: Sistema (por defecto) / Claro / Oscuro. Reemplaza el "tema claro único" de 0.5. La app tiene 0 colores sueltos fuera de `Paleta` (`ui/Tema.kt`), así que el trabajo es volver `Paleta` dinámica (CompositionLocal), definir la paleta oscura (`darkColorScheme`, misma identidad: oliva y dorado, fondo oscuro cálido, buen contraste) y cambiar el tema del Manifest (hoy fuerza `Theme.Material.Light`).
- **R10 versión mínima**: campo opcional de texto ("solo 20 min de gym"). Se pide al crear/editar un hábito propio y también para Gym (editable en Ajustes). Se muestra en la tarjeta de Hoy y en el Detalle. Texto de invitación, nunca obligación (R8).
- **R6 automatismo**: 4 preguntas, escala 1–5, a todos los hábitos de tipo HACER (Gym, Comida, Agua y propios HACER; no Ánimo ni DEJAR). Toca cada 28 días desde el inicio del hábito o desde el último check. Se avisa con una tarjeta en Hoy (sin notificaciones del sistema; se puede dejar para después) y el resultado se ve en el Detalle como puntaje y gráfico simple. Lenguaje no controlador (R8); se describe como "qué tan automático se siente", nunca como nota.
Datos: Room v7 (`perfil.tema`), v8 (`versionMinima` en `habito_propio` y `perfil`), v9 (tabla `automatismo`); una `Migration` por versión, solo ALTER/CREATE; copia de seguridad v7, v8 y v9 (las anteriores se siguen leyendo).

- [x] T27: Paleta dinámica y modo oscuro siguiendo al sistema: `Paleta` pasa a CompositionLocal con juego claro y oscuro, `TemaRomTracker` elige según `isSystemInDarkTheme()`, Manifest sin forzar tema claro (barra de estado y diálogos legibles en oscuro), revisar Hoy, Detalle, Resumen, Ajustes, Copia, Bienvenida, Nuevo hábito y diálogos. Sin migración ni cambios de lógica. Versión 0.8.0. Verifica en el celular (poner el celular en modo oscuro y recorrer todas las pantallas).
- [x] T28: Selector de tema: `Perfil.tema` (SISTEMA/CLARO/OSCURO, por defecto SISTEMA) con `Migration(6,7)` (Room v7), selector de 3 opciones en Ajustes con efecto inmediato, copia de seguridad v7 (las 1–6 se leen con SISTEMA), tests (`CopiaTest`). Versión 0.8.1. Verifica en el celular.
- [x] T29: Datos de versión mínima: columna `versionMinima` (texto, opcional) en `habito_propio` y en `perfil` (Gym) con `Migration(7,8)` (Room v8), funciones de guardado, copia de seguridad v8, tests. Sin UI todavía: el APK no cambia para el usuario. Versión 0.8.3 (sin cambio visible; la 0.8.2 ya se usó en ajustes de tema).
- [x] T30: UI de versión mínima: campo opcional en Nuevo hábito y en Editar hábito propio, campo para Gym en Ajustes, visible en la tarjeta de Hoy y en el Detalle. Versión 0.8.4. Verifica en el celular.
- [x] T31: Datos del automatismo: tabla `automatismo` (habito, fecha, 4 respuestas 1–5) con `Migration(8,9)` (Room v9), DAO, lógica pura (qué hábitos HACER toca chequear: 28 días desde inicio o último check; puntaje promedio), copia de seguridad v9, tests (`AutomatismoTest`, `CopiaTest`). Sin UI todavía. Versión 0.8.5 (sin cambio visible).
- [ ] T32: UI del automatismo: tarjeta en Hoy "¿Hacemos el check de [hábito]?" (con "Después"), pantalla de 4 preguntas con escala 1–5, y en el Detalle el último puntaje con gráfico simple de evolución. Versión 0.9.0. Verifica en el celular.

Criterios de aceptación 0.8: (1) con el celular en modo oscuro, ninguna pantalla ni diálogo queda ilegible o con colores claros sueltos; (2) Ajustes permite elegir Sistema/Claro/Oscuro y se mantiene al reabrir; (3) tras instalar encima de 0.7.3 no se pierde ningún dato; (4) la versión mínima se guarda, se muestra en Hoy y Detalle y puede quedar vacía; (5) el check de automatismo aparece solo en hábitos HACER cuando toca, se puede posponer y su resultado se ve en el Detalle; (6) copia de seguridad hecha en 0.8/0.9 se restaura y copias 0.7 o anteriores siguen restaurando.
