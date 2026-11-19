# Reglas y Mapa del Proyecto — RomTracker

## 1. Objetivo Core
- RomTracker: app Android (APK instalable sin Play Store) para crear hábitos saludables y extinguir malos hábitos con base científica, pensada para personas agotadas que solo pueden actuar después del trabajo.
- El seguimiento respeta las fases reales del cerebro (0–90, 90–270, 270+ días), no el mito de los 21 días.

## 2. Reglas de Contexto y Ahorro de Tokens (CRÍTICO)
- NUNCA leas carpetas completas ni todos los archivos de `docs/` de golpe.
- Al iniciar sesión, lee únicamente `docs/PROGRESS.md` y `docs/index.md`.
- Solo abre archivos específicos de `docs/notas/` cuando la tarea activa lo requiera explícitamente.
- Antes de escribir código o redactar entregables largos, verifica el criterio de aceptación en `docs/SPEC.md`.
- Una sola tarea `- [ ]` de `docs/SPEC.md` por sesión limpia (`/clear`). Si exploras >3 archivos, delega a un subagente Explore.
- `MASTER PROMPT.md` e `INFORME_HABITOS.md` son fuentes crudas: NO releerlas; usar sus notas destiladas en `docs/notas/`.

## 3. Reglas de Desarrollo (destiladas del Master Prompt)
- Ciclos cortos: no avanzar de etapa sin aprobación del usuario (salvo tareas técnicas menores).
- Implementar solo lo aprobado; no agregar funciones por iniciativa propia ni eliminar existentes sin consultar.
- No reemplazar código funcional innecesariamente; explicar antes de cambios importantes.
- Simplicidad, bajo costo, herramientas gratuitas, pocas dependencias, código mantenible.
- NUNCA inventar resultados de compilación, tests o archivos generados. Si hay errores, analizar el mensaje real.
- El usuario no es desarrollador profesional: explicaciones prácticas, comandos en bloques separados indicando dónde ejecutarlos.
- Si falta información del usuario, preguntar antes de asumir. Si su planteo tiene un error, decirlo y proponer alternativa.
- Destino: Android físico del usuario. Usar datos ficticios para pruebas.

## 4. Ciclo Iterativo (Etapa 5 del Master Prompt)
A. Evaluación → B. Priorización (correcciones / diseño / funciones / arquitectura) → C. Plan corto con archivos y riesgos
→ D. Implementar solo lo aprobado → E. Validar (compilar/tests) → F. Nuevo APK con versión y cambios → G. Qué probar en el celular.

## 5. Formato de Respuesta
1. Qué hacemos y por qué (breve). 2. Decisiones que requieren aprobación.
3. Indicar claramente cuándo probar en el celular. 4. Terminar con el próximo paso concreto.
5. No mezclar listas largas de tareas futuras con la tarea actual.

## 6. Comandos de Verificación
- Tests + APK debug (los corre GitHub Actions en cada push a `main`; NO compilar en este entorno): `./gradlew testDebugUnitTest assembleDebug`
- Salida esperada del APK: `app/build/outputs/apk/debug/app-debug.apk`

## 7. Mapa de Documentación (Leer bajo demanda)
- Bitácora de estado, versión y próxima tarea (reemplaza a PROJECT_PROGRESS.md): `docs/PROGRESS.md`
- Especificación activa (MVP, diseño, tecnología, tareas): `docs/SPEC.md`
- Índice del Segundo Cerebro: `docs/index.md`
- Instrucciones de instalación/compilación para humanos: `README.md` (crear en Fase 4)
