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
- [ ] T16: Datos y lógica — habilitar hábitos propios en Room (migración 4→5, nombre/ancla/por qué/tipo), registro Sí/No para hábitos propios, regla de ✓ según tipo, reutilizar fase/consistencia/racha, copia de seguridad v5, con tests. Verifica: Action en verde.
- [ ] T17: UI — pantalla "Nuevo hábito" (nombre, tipo, ancla, por qué), tarjetas propias en Hoy y Detalle con calendario. Verifica en el celular.
- [ ] T18: Gestión — editar y borrar (con confirmación) un hábito propio. Verifica en el celular.
