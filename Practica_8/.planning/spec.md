Analiza y completa el proyecto Android Studio ubicado en la carpeta `practica_8`.

El proyecto también se llama `practica_8`.

Usa el PDF proporcionado como especificación principal de la práctica. Si existe alguna diferencia entre tu conocimiento general y lo solicitado en el PDF, conserva la estructura y los requisitos de la práctica, excepto cuando sea necesario ajustar versiones de dependencias para que sean compatibles con el proyecto real generado por Android Studio.

Trabaja directamente sobre la estructura existente del proyecto generado por Android Studio. No crees un proyecto nuevo y no cambies el nombre del proyecto.

OBJETIVO GENERAL

Implementar una aplicación Android con Jetpack Compose y persistencia local usando Room Database sobre SQLite.

La aplicación debe permitir:

- Registrar usuarios.
- Guardarlos en una base de datos local.
- Mostrar los usuarios registrados en una lista dinámica.
- Actualizar automáticamente la interfaz cuando cambien los datos.
- Eliminar usuarios como parte del reto entregable.

ESTRUCTURA REQUERIDA

Dentro del paquete principal del proyecto crea la siguiente estructura:

practica_8
└── app
    └── src
        └── main
            └── java
                └── <paquete_real_del_proyecto>
                    ├── data
                    │   ├── Usuario.kt
                    │   └── UsuarioDao.kt
                    ├── domain
                    │   └── AppDatabase.kt
                    ├── presentation
                    │   └── MainScreen.kt
                    ├── ui.theme
                    │   └── archivos existentes del tema
                    └── MainActivity.kt

IMPORTANTE:

- Conserva el package/namespace real generado por Android Studio.
- No asumas que será `com.example.practica08`.
- Inspecciona primero el proyecto y utiliza el package real.
- No elimines los archivos de tema generados por Android Studio.
- No cambies el nombre del proyecto.
- No agregues arquitectura adicional que no sea necesaria.

CONFIGURACIÓN DE GRADLE Y KSP

1. Inspecciona primero las versiones reales de:

- Kotlin
- Android Gradle Plugin
- Gradle
- Jetpack Compose

2. Configura KSP (Kotlin Symbol Processing).

3. Antes de agregar una versión de KSP, verifica que sea compatible con la versión de Kotlin que usa actualmente el proyecto.

4. No actualices Kotlin, Gradle, Android Gradle Plugin o Compose salvo que sea estrictamente necesario para que Room/KSP funcionen.

5. Configura Room Database.

Agrega las dependencias necesarias:

- androidx.room:room-runtime
- androidx.room:room-ktx
- androidx.room:room-compiler usando KSP

6. No dupliques versiones de KSP entre el Gradle de proyecto y el Gradle del módulo de forma incompatible.

7. Después de modificar Gradle, realiza la sincronización correspondiente.

8. Si el proyecto presenta el problema indicado en la práctica relacionado con métodos abstractos o source sets, agrega en `gradle.properties`:

android.disallowKotlinSourceSets=false

ARCHIVO: Usuario.kt

Ubicación:

data/Usuario.kt

Debe representar una entidad Room llamada:

usuarios

Debe contener:

- id
- nombre

Requisitos:

- id debe ser clave primaria.
- id debe ser autogenerado.
- nombre debe almacenar el nombre del usuario.

La entidad debe ser compatible con el uso:

Usuario(nombre = nuevoNombre)

ARCHIVO: UsuarioDao.kt

Ubicación:

data/UsuarioDao.kt

Debe ser una interfaz DAO de Room.

Debe contener los siguientes métodos:

1. obtenerTodos()

Debe ejecutar:

SELECT * FROM usuarios

Debe devolver:

Flow<List<Usuario>>

2. insertar(usuario)

Debe utilizar:

@Insert

Debe ser:

suspend

3. eliminar(usuario)

Debe utilizar:

@Delete

Debe ser:

suspend

La función eliminar forma parte del reto entregable de la práctica.

ARCHIVO: AppDatabase.kt

Ubicación:

domain/AppDatabase.kt

Debe:

- heredar de RoomDatabase.
- registrar la entidad Usuario.
- utilizar version = 1.
- exponer:

usuarioDao(): UsuarioDao

Debe implementar un patrón Singleton para evitar abrir varias instancias de la base de datos.

Debe utilizar:

Room.databaseBuilder

Debe utilizar como nombre de base de datos:

mi_base_datos

Debe utilizar:

context.applicationContext

para crear la base.

ARCHIVO: MainScreen.kt

Ubicación:

presentation/MainScreen.kt

Debe contener toda la interfaz principal usando Jetpack Compose.

Debe obtener:

- el contexto con LocalContext.current.
- la instancia de AppDatabase.
- el UsuarioDao.
- un CoroutineScope mediante rememberCoroutineScope().

Debe obtener los usuarios mediante:

dao.obtenerTodos()

y convertir el Flow en estado de Compose mediante:

collectAsState()

La interfaz debe contener:

1. Un OutlinedTextField.

Debe permitir escribir el nombre del usuario.

El label debe ser:

Nombre del usuario

2. Un botón.

Texto:

Guardar Usuario

Al presionar el botón:

- verificar que el nombre no esté vacío.
- lanzar una corrutina.
- guardar el usuario utilizando:

dao.insertar(
    Usuario(
        nombre = nuevoNombre
    )
)

- realizar la escritura en un hilo apropiado, utilizando Dispatchers.IO cuando corresponda.
- después de guardar, regresar al hilo principal si es necesario.
- limpiar el campo de texto.

3. Un texto que diga:

Lista de Usuarios Registrados:

4. Un LazyColumn.

Debe mostrar todos los usuarios guardados.

Cada usuario debe mostrarse en una Card.

El texto debe tener el formato:

ID: <id> | Nombre: <nombre>

La lista debe actualizarse automáticamente cuando se inserten o eliminen datos.

RETO ENTREGABLE: ELIMINAR USUARIOS

Implementa también el reto completo de la práctica.

Cada Card debe contener una Row.

Dentro de la Row debe mostrarse:

- la información del usuario.
- un botón o icono para eliminar.

Al presionar eliminar:

- utilizar coroutineScope.launch.
- ejecutar dao.eliminar(usuario).
- realizar la operación en Dispatchers.IO cuando corresponda.

Después de eliminar, la lista debe actualizarse automáticamente mediante el Flow de Room y collectAsState().

No debe ser necesario recargar manualmente la pantalla.

ARCHIVO: MainActivity.kt

Mantén MainActivity lo más simple posible.

Dentro de:

setContent {
}

debe llamarse únicamente a:

MainScreen()

Importa MainScreen desde el package presentation.

No agregues lógica de base de datos directamente en MainActivity.

ARQUITECTURA

No agregues:

- ViewModel
- Repository
- Hilt
- Navigation
- Clean Architecture adicional
- Retrofit
- Firebase
- librerías innecesarias

Para esta práctica utiliza únicamente la estructura solicitada:

data
domain
presentation

más los archivos que Android Studio ya haya generado.

VALIDACIONES

Antes de terminar:

1. Revisa todos los imports.
2. Revisa todos los packages.
3. Revisa las dependencias.
4. Revisa la configuración de KSP.
5. Revisa que Room genere correctamente su código.
6. Revisa que no existan referencias rotas.
7. Revisa que Usuario, UsuarioDao y AppDatabase estén correctamente conectados.
8. Revisa que MainScreen pueda insertar datos.
9. Revisa que MainScreen pueda mostrar datos.
10. Revisa que MainScreen pueda eliminar datos.
11. Revisa que la lista se actualice automáticamente.
12. Revisa que MainActivity cargue correctamente MainScreen.

COMPILACIÓN

Si el entorno lo permite:

- sincroniza Gradle.
- compila el proyecto.
- corrige todos los errores de compilación encontrados.
- no dejes errores de imports, dependencias o packages sin corregir.

No modifiques archivos innecesariamente.

No reemplaces el proyecto completo.

No cambies configuraciones que ya funcionan si no es necesario.

ENTREGA FINAL

Cuando termines, dame un resumen claro con:

- archivos creados.
- archivos modificados.
- dependencias agregadas.
- configuración de KSP utilizada.
- cambios realizados en Gradle.
- cambios realizados en gradle.properties, si hubo.
- package real utilizado.
- si la compilación fue exitosa.
- errores encontrados y corregidos.
- cualquier problema que todavía requiera intervención manual.

El resultado final debe dejar el proyecto `practica_8` listo para abrir, sincronizar, ejecutar y presentar como la Práctica 08 de SQLite / RoomDatabase.