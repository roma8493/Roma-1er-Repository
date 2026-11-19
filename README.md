# RomTracker

App Android para crear hábitos saludables (gimnasio, comida sana) y soltar los que no suman, pensada para quien solo puede actuar después del trabajo. El seguimiento respeta las fases reales del cerebro (0–90, 90–270 y 270+ días). Los datos quedan solo en tu celular y funciona sin Internet.

Versión actual: **0.1.0** (Android 8 o superior).

## Instalar en el celular (sin Play Store)

1. Entrá al repositorio en GitHub → pestaña **Actions** → abrí la última ejecución en verde de **Build APK**.
2. Al final de la página, en **Artifacts**, descargá **RomTracker-APK** (es un .zip).
3. Descomprimilo: adentro está `app-debug.apk`. Pasalo al celular (cable, Drive, mail).
4. En el celular, abrí el archivo y aceptá "Instalar apps de esta fuente" si te lo pide.
5. Para actualizar, instalá el APK nuevo **encima** del anterior: no se pierden los datos.

Si desinstalás la app, se borran los datos.

## Compilar

No hace falta compilar a mano: GitHub Actions lo hace en cada push a `main` con

```
./gradlew testDebugUnitTest assembleDebug
```

La salida queda en `app/build/outputs/apk/debug/app-debug.apk`. Si querés compilar en tu computadora, necesitás JDK 17 y el SDK de Android.

El APK usa una clave de depuración fija (`app/debug.keystore`, no es secreta) para que las versiones nuevas se instalen sobre las anteriores.

## Documentación

- `docs/SPEC.md`: especificación y tareas.
- `docs/PROGRESS.md`: estado y decisiones.
