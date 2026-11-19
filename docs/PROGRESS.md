# Bitácora de Progreso — RomTracker (Memoria entre Sesiones)

> Reemplaza a `PROJECT_PROGRESS.md` del Master Prompt. Actualizar al cerrar cada tarea/iteración.

## Ficha del Proyecto
- **Nombre**: RomTracker
- **Objetivo**: App Android (APK sin Play Store) de hábitos basada en ciencia, para usuarios que solo pueden actuar después del trabajo.
- **Tecnología**: Kotlin + Jetpack Compose + Room; compila GitHub Actions (ver Etapa 3 en `docs/SPEC.md`).
- **Versión actual**: 0.1.0-T8 (versionCode 5; Bienvenida + Hoy + Detalle con calendario).
- **Estado**: Fases 1 y 2 completadas. Fase 3 completa (Etapas 1–3 aprobadas en `docs/SPEC.md`). Fase 4: T4–T7 hechas; T8 implementada, falta probar en el A34.

## Última Actualización
- **Fecha**: 2026-11-19
- **Resumen**: T8 implementada (`ui/Detalle.kt`): fase, barra, consistencia, racha, calendario mensual (editables hoy y 7 días atrás, no antes del inicio del hábito; menú Fui/No fui/Sin dato) y clave ancla/por qué en Gym; tarjetas de Hoy abren el Detalle; `esEditable` con tests. T7 confirmada en el A34. Antes, T7 implementada (`ui/Hoy.kt`): tarjetas Gym/Comida con fase y barra, botones que registran en 1 toque y corrigen, checklist "para mañana" y "lo que preparaste anoche"; tocar tarjeta → Detalle queda para T8. T6 confirmada en el A34 (persiste tras reiniciar). Antes, T6 hecha: Bienvenida (nombre+hora de salida, clave ancla, por qué) en `ui/Bienvenida.kt`; al pulsar Empezar guarda Perfil y crea el hábito GYM (inicio = hoy) en Room; sin perfil la app siempre vuelve al paso 1. Pendiente confirmar Action y probar en el A34. Antes, T5 hecha: capa Room (Perfil, Habito, Registro, Checklist + DAOs) en `data/` y lógica pura de fase/consistencia/racha en `logic/`, con 13 tests. Action en verde (run 44446995312, commit 6655abc). T4 confirmada en el A34 (instala y abre).

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

## Problemas Conocidos
- Corregido en T7: el contenido se dibujaba bajo la barra de estado/cámara frontal (Android 15 fuerza pantalla completa); se agregó `safeDrawingPadding()` en `MainActivity.kt`. Falta confirmar en el A34.

## Últimos Cambios
- Creados `.claude/settings.json`, `CLAUDE.md`, `docs/index.md`, `docs/PROGRESS.md`.
- Creadas 3 notas en `docs/notas/` (producto, ciencia-habitos, reglas-app-desde-ciencia), registradas en `docs/index.md`.

## Cómo Compilar e Instalar
- Pendiente (se completa en Fase 4 al generar el APK 0.1).

## Checklist de Tareas Activas
- [x] Fase 1: Cimientos (settings, CLAUDE.md, index, PROGRESS).
- [x] Fase 2: Crear `docs/notas/producto.md`.
- [x] Fase 2: Crear `docs/notas/ciencia-habitos.md`.
- [x] Fase 2: Crear `docs/notas/reglas-app-desde-ciencia.md` (reglas R1–R12).
- [x] Fase 3 / Etapa 1: MVP 0.1 en `docs/SPEC.md` (aprobado 2026-11-19).
- [x] Fase 3 / Etapa 2: diseño y prototipo (pantallas y flujo de R-01 a R-06).
- [x] Fase 3 / Etapa 3: tecnología y comando de compilación definitivo.
- [ ] Fase 4: tareas T8–T9 (T4–T7 hechas) de `docs/SPEC.md` (APK 0.1) y luego ciclos iterativos (Etapa 5).

## Próximo Paso
- Esperar Action en verde, instalar APK 0.1.0-T8 en el A34 y probar Detalle; luego marcar T8, `/clear` y T9 (revisión de textos, APK final, README).
