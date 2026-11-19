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

## Etapa 2 — Diseño y prototipo
- Pendiente (próxima sesión).

## Etapa 3 — Tecnología
- Pendiente (definirá lenguaje/framework, almacenamiento local y dónde se compila).

## Tareas Atómicas
- [x] T1: Redactar y aprobar Etapa 1 (MVP 0.1).
- [ ] T2: Etapa 2 — pantallas y flujo (bocetos) de R-01 a R-06.
- [ ] T3: Etapa 3 — elegir tecnología y comando de compilación definitivo.
- [ ] (Las tareas de implementación del APK 0.1 se agregan al aprobar la Etapa 3.)
