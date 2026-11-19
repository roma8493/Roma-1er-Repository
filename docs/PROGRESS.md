# Bitácora de Progreso — RomTracker (Memoria entre Sesiones)

> Reemplaza a `PROJECT_PROGRESS.md` del Master Prompt. Actualizar al cerrar cada tarea/iteración.

## Ficha del Proyecto
- **Nombre**: RomTracker
- **Objetivo**: App Android (APK sin Play Store) de hábitos basada en ciencia, para usuarios que solo pueden actuar después del trabajo.
- **Tecnología**: Pendiente (se decide en Etapa 3 dentro de `docs/SPEC.md`).
- **Versión actual**: Ninguna (sin APK todavía).
- **Estado**: Fases 1 (Cimientos) y 2 (Segundo Cerebro) completadas. Próximo: Fase 3 (SPEC).

## Última Actualización
- **Fecha**: 2026-11-19
- **Resumen**: Inicialización de la arquitectura de Context & Harness Engineering. Unificadas las reglas de la skill /arquitecto con el Master Prompt.

## Funcionalidades
- **Implementadas**: ninguna.
- **Pendientes**: se definen en el MVP (Etapa 1, `docs/SPEC.md`).

## Decisiones Clave y Reglas Aprendidas
- Mantener `CLAUDE.md` por debajo de 100 líneas.
- Ejecutar una sola tarea de `docs/SPEC.md` por cada sesión limpia (`/clear`).
- Única bitácora: este archivo (no crear `PROJECT_PROGRESS.md`).
- Mapeo de marcos: Fase 3 (SPEC) = Etapas 1–3 del Master Prompt (MVP, diseño, tecnología); Fase 4 = Etapas 4–5 (APK 0.1 + ciclos A–G).
- Modelo: Opus 5.5 para planificación (Fases 2–3); Sonnet 5.5 para ejecución (Fase 4), cuando Claude lo indique.
- Fuentes crudas (`MASTER PROMPT.md`, `INFORME_HABITOS.md`) no se releen; usar `docs/notas/`.

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
- [ ] Fase 3: `/plan` → entrevista y `docs/SPEC.md` (Etapa 1 MVP → Etapa 2 Diseño/prototipo → Etapa 3 Tecnología), con aprobación en cada etapa.
- [ ] Fase 4: APK 0.1 (Etapa 4) y ciclos iterativos (Etapa 5).

## Próximo Paso
- Fase 3: `/plan` → entrevista (resolver puntos abiertos de `notas/producto.md`) y redactar `docs/SPEC.md` empezando por Etapa 1 (MVP).
