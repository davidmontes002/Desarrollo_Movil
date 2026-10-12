# Mini2 — Administrador de Tareas Locales

Aplicación académica en Kotlin y Jetpack Compose. Guarda tareas en Room/SQLite y actualiza la interfaz mediante Flow. No necesita conexión a Internet.

## Abrir y ejecutar

1. En Android Studio: **File > Open** y seleccionar `/home/simi/AndroidStudioProjects/Mini2`.
2. Ejecutar **Sync Project with Gradle Files**.
3. Elegir el módulo `app` y un emulador o teléfono Android 7.0/API 24 o superior.
4. Pulsar **Run**. El proyecto conserva el SDK 37 y las versiones que generó Android Studio.

Desde terminal, situado en esa carpeta:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Estructura

```text
app/src/main/java/com/example/mini2/
├── MainActivity.kt
├── data/
│   ├── Tarea.kt
│   └── TareaDao.kt
├── domain/
│   └── AppDatabase.kt
├── presentation/
│   ├── MainScreen.kt
│   ├── DialogoTarea.kt
│   ├── TareaItem.kt
│   ├── MenuTareas.kt
│   └── FiltrosTareas.kt
└── ui/theme/                  (archivos originales)
```

`MainActivity` solo carga `MainScreen`. Esta obtiene el DAO singleton, aplica `Mini2Theme` y muestra `PantallaTareas`. Los componentes se separan para facilitar la lectura y probar la pantalla con una base aislada.

## Datos y funcionamiento

- `Tarea`: `id` autogenerado, `titulo`, `descripcion`, `completada` (false inicialmente) y `fechaCreacion` (milisegundos).
- BD `mini2_database`, versión 1. Singleton con `applicationContext`, `@Volatile` y `synchronized`.
- DAO: `obtenerTodas()` devuelve `Flow<List<Tarea>>`; `insertar`, `actualizar`, `eliminar` y `eliminarCompletadas` son `suspend`.
- `+` abre el formulario. Solo admite un título no vacío ni compuesto únicamente por espacios; la descripción es opcional.
- Editar conserva id, fecha y estado. El mismo diálogo permite alta y edición; Cancelar no escribe cambios.
- Checkbox cambia entre pendiente y completada. Eliminar borra la tarea individual por id.
- Menú: Todas, Pendientes, Completadas, Ordenar por fecha, Ordenar por estado y Borrar completadas.
- Fecha: más recientes primero. Estado: pendientes primero, con fecha descendente dentro de cada grupo. Los empates se resuelven por id descendente.
- Borrar completadas pide confirmación y ejecuta `DELETE FROM tareas WHERE completada = 1`, independientemente del filtro activo.
- Filtros y orden se derivan de la lista de Room. No hay una segunda lista mutable. Toda escritura usa corrutinas/IO; Room vuelve a emitir y `collectAsState` actualiza la pantalla.
- Filtro, orden y borrador resisten recreaciones de pantalla. Los datos se guardan en disco; el filtro/orden no son preferencias permanentes entre cierres completos del proceso.

## Configuración preservada y dependencias

| Componente | Versión |
|---|---|
| Kotlin y plugin Compose | 2.2.10 |
| Android Gradle Plugin | 9.3.3 |
| Gradle Wrapper | 9.5.0 |
| Compose BOM | 2026.02.01 |
| Material 3, resuelto por BOM | 1.4.0 |
| Room runtime/ktx/compiler | 2.8.4 |
| KSP2 | 2.3.4 |

KSP se define una sola vez en `gradle/libs.versions.toml`, se declara con `apply false` en el Gradle raíz y se aplica en `app`. Room compiler usa `ksp(...)`. Los iconos básicos (`material-icons-core`) toman su versión del BOM. No se agrega el paquete extendido.

Se modificaron los dos `build.gradle.kts`, `libs.versions.toml` y `MainActivity.kt`. Se conservan `settings.gradle.kts`, `gradle.properties`, wrapper, manifest y archivos de tema.

## Verificar manualmente

1. Crear tres tareas en distintos momentos, una con descripción y otra sin descripción. Intentar guardar un título vacío o solo espacios.
2. Editar título y descripción; comprobar que sigue siendo la misma tarea.
3. Completar una tarea y volverla a marcar pendiente. Editar una completada y comprobar que conserva su estado.
4. Probar los tres filtros y ambos órdenes desde el menú.
5. Cerrar completamente Mini2 y reabrirla. Confirmar títulos, descripciones y estados.
6. Eliminar una tarea, reabrir y comprobar que ya no aparece.
7. Dejar tareas pendientes y completadas; usar Borrar completadas. Probar Cancelar primero y luego confirmar. Solo deben desaparecer las completadas.
8. Crear suficientes tareas para desplazar la lista. Girar el teléfono mientras se edita un borrador.

Para el documento que elaborarás, conviene capturar: lista vacía; formulario nuevo; lista con tareas pendientes y completadas; diálogo de edición; menú abierto; filtro sin resultados; orden por estado; confirmación de borrado y lista posterior; lista conservada después de reabrir. Este proyecto no incluye capturas ni PDF de entrega.

## Pruebas automatizadas

```bash
./gradlew :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug
```

- `FiltrosTareasTest`: filtros, ambos órdenes, empate de fechas y lista vacía.
- `TareaDatabaseTest`: cada emisión de Flow y persistencia de CRUD/estado/borrado selectivo al reabrir SQLite.
- `PantallaTareasTest`: formulario, edición, checkbox, menú, filtros, orden, borrado selectivo, restauración del borrador y desplazamiento de LazyColumn; usa SQLite en memoria aislado.
- `PersistenciaProcesoTest`: permite verificar cambios en la base real por fases entre cierres de proceso. Solo usa registros con un prefijo de prueba y los elimina al concluir.
- Se conservan las pruebas de plantilla.

Las referencias 06, 07 y 08 guiaron los componentes y el acceso directo a Room. Los demás PDF se consultaron como contexto. Los retos particulares de las prácticas no amplían el alcance de Mini2.

## Resultado de verificación (11 de octubre de 2026)

- `assembleDebug`: exitoso; Room generó `TareaDao_Impl` y `AppDatabase_Impl`.
- 5 pruebas locales aprobadas (4 de filtros/orden + 1 de plantilla).
- 7 pruebas instrumentadas aprobadas en moto g53 5G, Android 14, sin fallos ni omisiones.
- 6 fases adicionales aprobadas con cierre completo del proceso y reapertura real de MainActivity entre fases: crear, editar, completar, desmarcar, eliminar y verificar/limpiar. Evidencia: `.planning/process-test-results.txt`.
- `lintDebug`: 0 errores, 15 advertencias no bloqueantes (versiones más recientes, colores sin uso de la plantilla y etiqueta redundante).
- Durante las pruebas se corrigieron un import innecesario de `assertDoesNotExist` y un selector ambiguo que encontraba la descripción en la tarjeta y en el diálogo. No fue necesario cambiar Kotlin/AGP/Gradle/Compose ni agregar opciones a `gradle.properties`.
- APK instalado y MainActivity abierta satisfactoriamente en el teléfono. Registros y paquete de pruebas retirados. No quedan bloqueos técnicos conocidos.
