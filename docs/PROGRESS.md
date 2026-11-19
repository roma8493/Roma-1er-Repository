# Bitácora de Progreso — RomTracker (Memoria entre Sesiones)

> Reemplaza a `PROJECT_PROGRESS.md` del Master Prompt. Actualizar al cerrar cada tarea/iteración.

## Ficha del Proyecto
- **Nombre**: RomTracker
- **Objetivo**: App Android (APK sin Play Store) de hábitos basada en ciencia, para usuarios que solo pueden actuar después del trabajo.
- **Tecnología**: Pendiente (se decide en Etapa 3 dentro de `docs/SPEC.md`).
- **Versión actual**: Ninguna (sin APK todavía).
- **Estado**: Fases 1 y 2 completadas. Fase 3: Etapa 1 (MVP 0.1) aprobada en `docs/SPEC.md`. Próximo: Etapa 2 (diseño).

## Última Actualización
- **Fecha**: 2026-11-19
- **Resumen**: Entrevista de Fase 3 hecha; creado `docs/SPEC.md` con la Etapa 1 (MVP 0.1) aprobada.

## Funcionalidades
- **Implementadas**: ninguna.
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

## Problemas Conocidos
- Comando de compilación `./gradlew assembleDebug` es provisorio hasta aprobar la tecnología.
- Falta verificar si este entorno puede compilar APK (JDK / Android SDK) o si se compila en la PC del usuario.

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
- [ ] Fase 3 / Etapa 2: diseño y prototipo (pantallas y flujo de R-01 a R-06).
- [ ] Fase 3 / Etapa 3: tecnología y comando de compilación definitivo.
- [ ] Fase 4: APK 0.1 (Etapa 4) y ciclos iterativos (Etapa 5).

## Próximo Paso
- Fase 3 / Etapa 2 (tarea T2 de `docs/SPEC.md`): en sesión limpia, `/plan` → proponer pantallas y flujo del MVP para aprobación.
