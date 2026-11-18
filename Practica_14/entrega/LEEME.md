# Práctica 14 — GPS y mapas
Proyecto Android Studio con Jetpack Compose, OSMdroid y Room. Se usa OpenTopoMap tal como el ejemplo del PDF; no necesita clave de Google Maps ni facturación.

## Ejecutar en el celular
1. Abrir este proyecto en Android Studio y sincronizar Gradle.
2. Conectar el celular por USB, habilitar depuración USB y aceptar la autorización.
3. Elegir el celular físico en el selector de dispositivos y pulsar Run.
4. Conceder ubicación precisa mientras se usa la app. Mantener Internet y ubicación activados.
5. El GPS rojo indica la posición; «Mi ubicación» vuelve a centrar el mapa.
6. Mantener pulsada una calle: se guarda un punto en Room. Tocar el marcador muestra título y coordenadas.
7. Cerrar y volver a abrir: los puntos se conservan.

## Correspondencia con el PDF
- Versiones y dependencias: gradle/libs.versions.toml y ambos build.gradle.kts.
- Cuatro permisos: app/src/main/AndroidManifest.xml.
- Mapa y permisos en ejecución: MapaOSMScreen.kt.
- Configuración OSMdroid y enlace: MainActivity.kt.
- Vector rojo convertido a Bitmap con setPersonIcon/setDirectionArrow: ic_gps_rojo.xml y MapaOSMScreen.kt.
- Entidad, inserción suspend, consulta Flow y singleton: MarcadorEntity.kt, MarcadorDao.kt, AppDatabase.kt.
- Pulsación larga, escritura y actualización reactiva: MapEventsOverlay y LaunchedEffect.
- Prueba de persistencia real: MarcadorPersistenceTest.kt.

## Compatibilidad
Se conservan Kotlin 2.2.10, Compose BOM 2026.02.01 y OSMdroid 6.1.18 del PDF.
Room se actualizó de 2.6.1 a 2.8.4 con autorización expresa del usuario: la versión del PDF rechaza los metadatos de Kotlin y dependencias actuales. No se necesitan ajustes temporales de lenguaje ni de coroutines.
El nombre visible es practica14; el paquete existente com.example.practica_14 se conserva.

## Fuentes técnicas
- https://developer.android.com/build/migrate-to-built-in-kotlin
- https://github.com/osmdroid/osmdroid

## Verificación en Moto g53 5G (Android 14)
- assembleDebug: correcto.
- lintDebug: 0 errores; advertencias no bloqueantes de dependencias/plantilla.
- connectedDebugAndroidTest: 2 pruebas aprobadas, incluida inserción suspend, emisión Flow y reapertura de Room.
- Verificación visual: calles OpenTopo descargadas, ubicación real y flecha roja.
- Pulsación larga: contador cambia de 0 a 1 y aparece el marcador.
- Toque sobre el marcador: muestra título, coordenadas e ID.
- Cierre completo con am force-stop y reapertura: marcador y contador permanecen.

## Entrega
El video practica14-reto.mp4 muestra la app real en el celular, la pulsación larga, el marcador guardado y su recuperación después de reiniciar el proceso. No tiene narración.
El APK practica14-debug.apk permite instalar la misma app.
El archivo practica14-codigo.zip contiene el código y configuración para Android Studio.
