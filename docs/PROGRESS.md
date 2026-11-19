# Bitácora de Progreso — RomTracker (Memoria entre Sesiones)

> Reemplaza a `PROJECT_PROGRESS.md` del Master Prompt. Actualizar al cerrar cada tarea/iteración.

## Ficha del Proyecto
- **Nombre**: RomTracker
- **Objetivo**: App Android (APK sin Play Store) de hábitos basada en ciencia, para usuarios que solo pueden actuar después del trabajo.
- **Tecnología**: Kotlin + Jetpack Compose + Room; compila GitHub Actions (ver Etapa 3 en `docs/SPEC.md`).
- **Versión actual**: Ninguna (sin APK todavía).
- **Estado**: Fases 1 y 2 completadas. Fase 3 completa (Etapas 1–3 aprobadas en `docs/SPEC.md`). Próximo: Fase 4, tarea T4.

## Última Actualización
- **Fecha**: 2026-11-19
- **Resumen**: T3 hecha: tecnología aprobada (Kotlin + Compose + Room, GitHub Actions, APK debug con clave fija). Tareas T4–T9 agregadas a `docs/SPEC.md`.

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
- Diseño 0.1: 3 pantallas (Bienvenida 3 pasos → Hoy ⇄ Detalle con calendario mensual). Sin ajustes en 0.1.
- Checklist ítem 3 = casilla "Decidí a qué hora voy" (sin elegir hora). Comida también tiene fase cerebral.
- Este entorno no tiene JDK/SDK y tiene 3 GB RAM: NO compilar acá; compila GitHub Actions en cada push a `main`.
- Clave debug fija en el repo para que los APK nuevos se instalen encima sin perder datos.

## Problemas Conocidos
- Sin verificar todavía: que el workflow de GitHub Actions compile (se prueba en T4).

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
- [ ] Fase 4: tareas T4–T9 de `docs/SPEC.md` (APK 0.1) y luego ciclos iterativos (Etapa 5).

## Próximo Paso
- Fase 4 / T4 (en sesión limpia, con Sonnet 5.5): esqueleto del proyecto Kotlin + Compose, clave debug fija y workflow de GitHub Actions.
