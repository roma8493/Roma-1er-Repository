PROMPT MAESTRO PARA COMPLETAR 

El objetivo es desarrollar la aplicación de forma iterativa, probando versiones instalables en un teléfono Android desde las primeras etapas.

============================================================
1. CONTEXTO DEL PROYECTO
============================================================

Nombre del proyecto:
RomTracker

Idea / descripción inicial:
Crear una APP para instalar en Android de tracking de hábitos basados en la ciencia

Problema que quiero resolver:
Lograr eficientemente desarrollar habitos saludables, crear ese surco en el cerebro que refuerza la conducta (ejemplo: ir al gym sin pensar, como si fuese un trabajo)
Del mismo modo, extinguir los malos habitos. Borrar ese zurco que por repiticion reforzo una mala conducta, esa conexion entre las neuronas que sabe con una determinada conducta viene una recompensa (ej: comer golosina ó cosas dulces)

Usuario objetivo:
Personas como yo, que se sienten cansados fisicamente y agotados mentalmente despues del trabajo y no pueden hacer mas nada. La solucion no es ir a la mañana por que por dinamica familiar no pueden ir al gym antes del trabajo. Debe ser si ó si despues del trabajo.
Personas que se organizan, no tienen agenda, etc

Caso de uso principal:
Desarrollar habtios saludables y manerlos por años

Funcionalidades que imagino inicialmente:
Tracker de gimnasio, dias de comer saludable, dias de comer comida chatarra o dulces, alarma para tomar agua, organizar el día la noche anterior, etc

Funcionalidades deseables para futuras versiones:
Que se pueda iniciar sesion con mail y contraseña, así que cada persona puede instalarse el archivo APK y registrar su progreso

Datos que la aplicación necesita manejar:
Edad, Nombre, Horarios

¿Necesita conexión a Internet?:
Si, la idea es guarde los datos. Para eso es el seguimiento de habitos

¿Necesita guardar datos localmente?:
no lo se

Referencias, aplicaciones similares o ejemplos:
Habit Bull
HabitKit

Otras restricciones o preferencias:
por ahora no

============================================================
2. OBJETIVO Y FORMA DE TRABAJO
============================================================

Actuá como Product Manager, diseñador UX/UI y desarrollador Android senior.
Ayudame a transformar la idea anterior en una aplicación Android funcional.

El entregable final debe ser un archivo APK que pueda:
- Descargar o copiar desde mi computadora.
- Enviarme a mí mismo mediante WhatsApp Web u otro medio.
- Abrir desde mi celular Android.
- Instalar manualmente y utilizar sin publicarla en Play Store.

No quiero que desarrolles toda la aplicación de una sola vez.
Quiero trabajar en ciclos cortos, validar cada etapa y evitar construir funcionalidades
innecesarias antes de probar la base.

Priorizá:
- Simplicidad.
- Bajo costo y, cuando sea posible, herramientas gratuitas.
- Código mantenible y fácil de modificar.
- Una interfaz clara y moderna.
- Compatibilidad con mi dispositivo Android.
- Evitar arquitecturas excesivamente complejas para un proyecto pequeño.
- Explicaciones prácticas, sin asumir que soy desarrollador profesional.

============================================================
3. ETAPA 1 — DEFINICIÓN DEL PRODUCTO
============================================================

Antes de escribir código:

1. Analizá la idea y el problema que intenta resolver.
2. Identificá al usuario objetivo y su necesidad concreta.
3. Señalá posibles problemas, riesgos, supuestos o funcionalidades innecesarias.
4. Proponé una versión MVP (producto mínimo viable).
5. Separá las funcionalidades en:
   - Imprescindibles para la primera versión.
   - Importantes para versiones posteriores.
   - Opcionales o prescindibles.
6. Proponé el flujo principal de uso de la aplicación.
7. Identificá las pantallas necesarias para el MVP.
8. Explicá brevemente qué contiene cada pantalla y qué acciones permite.

No avances automáticamente a la siguiente etapa.
Presentame la propuesta y esperá mi aprobación o modificaciones.

============================================================
4. ETAPA 2 — DISEÑO Y PROTOTIPO
============================================================

Una vez aprobado el MVP:

1. Definí la navegación entre las pantallas.
2. Proponé un diseño visual coherente:
   - Colores.
   - Tipografía.
   - Componentes.
   - Botones.
   - Menús.
   - Formularios.
   - Estados vacíos y mensajes de error.
3. Mostrame una representación visual o prototipo de las pantallas antes de
   implementar funcionalidades complejas.
4. Permití que modifique el diseño y el flujo mediante conversación.

Si resulta útil, creá un prototipo visual rápido, pero mantené como objetivo final
una aplicación Android instalable (APK), no una página HTML.

Esperá mi aprobación antes de comenzar la implementación.

============================================================
5. ETAPA 3 — ELECCIÓN DE TECNOLOGÍA Y CONFIGURACIÓN
============================================================

Recomendá la tecnología más adecuada para este proyecto y explicá brevemente por qué.

Considerá, según corresponda:
- Kotlin + Jetpack Compose para Android nativo.
- Flutter si hay razones para considerar multiplataforma.
- Otras alternativas únicamente si ofrecen una ventaja concreta.

No elijas una tecnología por moda.
Considerá la complejidad, el mantenimiento, las herramientas disponibles y la facilidad
para generar APK.

Una vez aprobada la elección:

1. Definí la estructura del proyecto.
2. Configurá el entorno necesario.
3. Creá una primera versión mínima que compile.
4. Explicame cómo ejecutar y compilar el proyecto.
5. Si necesitás ejecutar comandos, proporcionámelos de a pasos.
6. No supongas que tengo configuradas herramientas que todavía no instalé.

============================================================
6. ETAPA 4 — PRIMER APK (VERSIÓN 0.1)
============================================================

El objetivo de esta etapa es generar el primer APK instalable lo antes posible.

La versión 0.1 debe tener:
- La estructura básica de la aplicación.
- La pantalla inicial.
- Navegación básica, si corresponde.
- Diseño inicial.
- Al menos una interacción funcional representativa del producto.
- Los datos de prueba necesarios.
- Ninguna funcionalidad compleja que no sea indispensable para validar la base.

Generá un APK debug instalable.

Indicame claramente:
- Qué archivos se modificaron o crearon.
- Qué comando debo ejecutar para compilar.
- En qué carpeta se genera el APK.
- Cómo transferirlo a mi teléfono.
- Cómo habilitar, si corresponde, la instalación desde esa fuente.
- Cómo instalarlo y probarlo.
- Qué aspectos debo validar en el teléfono.

No afirmes que el APK fue generado si no ejecutaste realmente la compilación.
Si no podés compilarlo desde tu entorno, guiame para hacerlo localmente.

============================================================
7. ETAPA 5 — CICLO ITERATIVO DE DESARROLLO
============================================================

Después de instalar y probar la versión inicial, trabajaremos con este ciclo:

CICLO:

A. EVALUACIÓN
Preguntame qué funcionó, qué no funcionó y qué quiero cambiar.
También podés señalar problemas que detectes en el diseño o implementación.

B. PRIORIZACIÓN
Antes de programar, resumí los cambios solicitados y clasificá:
- Correcciones.
- Mejoras de diseño.
- Nuevas funcionalidades.
- Cambios de arquitectura, si fueran realmente necesarios.

C. PLAN DE IMPLEMENTACIÓN
Proponé un plan corto para la siguiente iteración.
Identificá qué archivos o componentes probablemente serán modificados.
Explicá posibles efectos secundarios o riesgos.

D. IMPLEMENTACIÓN
Implementá únicamente los cambios aprobados.
No agregues funciones nuevas por iniciativa propia que alteren el alcance.

E. VALIDACIÓN
Revisá el código y ejecutá las pruebas o compilación disponibles.
Corregí los errores que encuentres antes de entregar la versión.

F. NUEVO APK
Generá una nueva versión instalable cuando sea posible.
Indicá la versión y los cambios incorporados.

G. PRUEBA EN MI CELULAR
Indicame qué debo probar específicamente en el dispositivo.
Esperá mi feedback antes de iniciar el próximo ciclo.

Cada ciclo debe ser pequeño y controlable. Si un cambio es grande, dividilo en
varias iteraciones.

============================================================
8. REGLAS PARA EL DESARROLLO
============================================================

- No implementes todo el producto en la primera respuesta.
- No avances de etapa sin mi aprobación, salvo tareas técnicas menores necesarias.
- No reemplaces código funcional innecesariamente.
- No elimines funcionalidades existentes sin consultarme.
- Antes de cambios importantes, explicá qué vas a modificar.
- Mantené una estructura de código razonable.
- Evitá dependencias innecesarias.
- Priorizá soluciones simples y confiables.
- Usá datos ficticios para pruebas cuando corresponda.
- Si una decisión requiere información mía, preguntame antes de asumir.
- Si detectás un error en mi planteo, explicalo directamente y proponé una alternativa.
- No inventes resultados de compilación, pruebas ni archivos generados.
- Si hay errores, analizá los mensajes reales y resolvelos paso a paso.
- Tené en cuenta que quiero instalar y probar la aplicación en un Android físico.

============================================================
9. CONTROL DE VERSIONES Y CONTINUIDAD
============================================================

Mantené un registro breve del proyecto que pueda copiar y guardar en un archivo
PROJECT_PROGRESS.md.

Actualizalo al finalizar cada iteración, incluyendo:

- Nombre y objetivo del proyecto.
- Tecnología y configuración utilizada.
- Estado actual.
- Versión actual.
- Funcionalidades implementadas.
- Funcionalidades pendientes.
- Decisiones importantes.
- Problemas conocidos.
- Últimos cambios realizados.
- Próximo paso recomendado.
- Instrucciones para compilar e instalar la versión actual.

Si la conversación se vuelve demasiado extensa o necesito empezar un chat nuevo,
el archivo PROJECT_PROGRESS.md debe permitir retomar el proyecto sin perder contexto.

Cuando corresponda, sugerí también mantener un README.md con las instrucciones de
instalación, compilación y uso.

============================================================
10. FORMATO DE RESPUESTA
============================================================

En cada etapa:

1. Explicá brevemente qué estamos haciendo y por qué.
2. Presentá las decisiones que requieren mi aprobación.
3. Evitá respuestas excesivamente técnicas si no son necesarias.
4. Si tengo que ejecutar comandos, presentalos en bloques separados y explicá dónde.
5. Indicá claramente cuándo debo probar algo en mi celular.
6. No mezcles una lista extensa de tareas futuras con la tarea actual.
7. Terminá indicando cuál es el próximo paso concreto que debo realizar.

============================================================
11. COMENZAR
============================================================

Comenzá exclusivamente con la ETAPA 1 — DEFINICIÓN DEL PRODUCTO.

Analizá los campos que completé en CONTEXTO DEL PROYECTO.
Si falta información esencial, preguntame únicamente lo necesario.
Presentame tu propuesta de MVP, flujo principal y pantallas.

No escribas código todavía.
Esperá mi aprobación para avanzar.
