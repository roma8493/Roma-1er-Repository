# Reglas de la App derivadas de la Ciencia

> Catálogo de ideas. Qué entra al MVP se decide en `docs/SPEC.md` (Fase 3). Fundamento en [ciencia-habitos.md](ciencia-habitos.md).

| # | Hallazgo científico | Regla / función en la app |
|---|---|---|
| R1 | El hábito lo dispara una clave contextual | Todo hábito bueno se crea con su **clave ancla** ("Después de ___, voy a ___"), con hora/lugar. |
| R2 | La fuerza de voluntad se agota con fatiga (usuario post-trabajo) | **Plan de la noche anterior**: checklist para preparar bolso/ropa/comida; se decide descansado, no a las 19 h agotado. |
| R3 | Eliminar malos hábitos = quitar/cambiar la clave | Hábito malo se registra con **desencadenante** (lugar, hora, emoción, compañía); la app sugiere cambios del entorno, no "aguantar". |
| R4 | Los hábitos no se borran; la recaída es esperable | Un desliz **no reinicia a cero ni culpa**. Mostrar **consistencia** (ej. 80/90 días) además de la racha. |
| R5 | Plazos reales: 0–90 / 90–270 / 270+ días | Cada hábito muestra su **fase cerebral** (Adquisición / Consolidación / Automático) y barra hacia la siguiente. Prohibido mencionar "21 días". |
| R6 | Medir automatismo, no solo frecuencia (SRHI) | **Check de automatismo** cada 2–4 semanas: 2–4 preguntas tipo "¿lo hacés sin pensar?" (escala 1–5), con gráfico de evolución. |
| R7 | Justificación significativa → motivación autónoma | Al crear el hábito: "¿Por qué es importante para vos?"; se muestra en días difíciles. |
| R8 | Lenguaje no controlador + convicción en el éxito | Textos y notificaciones como invitaciones ("¿Vamos?"); nunca "¡No cumpliste!" ni "debés". |
| R9 | Revisión de metas y barreras | **Revisión semanal** breve: ¿qué te frenó? ¿qué cambiás del entorno? |
| R10 | Conductas simples se automatizan más rápido | Sugerir **versión mínima** del hábito al empezar (ej. "solo 20 min de gym"). |
| R11 | Clave motivada / antojo (dopamina) | Para hábitos malos: registrar el **antojo resistido** como logro, no solo el consumo. |
| R12 | Fase 1 (días 1–90) = riesgo máximo de abandono | Más apoyo/recordatorios en los primeros 90 días; menos intrusivo en fases siguientes. |

## Mapeo a funciones pedidas por el usuario
- Gym → hábito bueno (R1, R2, R5, R10).
- Comida sana vs. chatarra/dulces → hábito bueno + hábito malo con desencadenantes (R3, R4, R11).
- Agua → recordatorio recurrente (hábito simple, R10).
- Organizar el día la noche anterior → R2.
