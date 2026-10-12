Analiza y completa el proyecto Android Studio ubicado en la carpeta `Mini2`.

El proyecto también se llama `Mini2`.

Te proporcionaré como material de referencia los PDF de las prácticas 01 a 08 de Desarrollo Móvil Nativo.

IMPORTANTE SOBRE LOS PDF:

Los PDF deben utilizarse como REFERENCIA TÉCNICA para mantener la misma forma de trabajo, sintaxis, estructura, nivel de complejidad y componentes utilizados durante el curso.

Presta especial atención a:

- Práctica 06: LazyColumn y listas dinámicas.
- Práctica 07: Menús, TopAppBar, Scaffold y Material Design.
- Práctica 08: SQLite / Room Database.

Las prácticas anteriores pueden consultarse cuando sea necesario para respetar la forma en que se han construido los proyectos durante el curso.

NO debes realizar los retos, entregables adicionales o ejercicios particulares incluidos al final de los PDF solamente porque aparezcan en ellos.

Yo realizaré personalmente los puntos o retos individuales de las prácticas PDF.

Tu tarea es implementar únicamente el MINIPROYECTO 02 descrito a continuación.

==================================================
MINIPROYECTO 02
ADMINISTRADOR DE TAREAS LOCALES
==================================================

OBJETIVO GENERAL

Desarrollar una aplicación Android llamada `Mini2` utilizando Kotlin y Jetpack Compose.

La aplicación será un:

"Administrador de Tareas Locales"

Debe integrar los conceptos vistos durante las prácticas:

- Listas dinámicas.
- LazyColumn.
- Material Design.
- Scaffold.
- TopAppBar / ToolBar.
- Menús desplegables.
- Acciones en la barra superior.
- Room Database.
- Flow.
- Corrutinas.
- Persistencia local.

La aplicación debe permitir:

- Crear tareas.
- Visualizar tareas.
- Editar tareas.
- Marcar tareas como completadas o pendientes.
- Eliminar tareas.
- Eliminar todas las tareas completadas.
- Ordenar o filtrar las tareas.
- Conservar toda la información después de cerrar y volver a abrir la aplicación.

==================================================
FORMA DE TRABAJO
==================================================

Trabaja directamente sobre el proyecto existente generado por Android Studio dentro de la carpeta:

Mini2

NO crees otro proyecto.

NO cambies el nombre del proyecto.

NO elimines innecesariamente archivos generados por Android Studio.

Antes de modificar cualquier cosa:

1. Inspecciona el proyecto completo.
2. Identifica el package/namespace real.
3. Identifica las versiones actuales de:
   - Kotlin.
   - Android Gradle Plugin.
   - Gradle.
   - Jetpack Compose.
   - Material 3.
4. Revisa la estructura existente.
5. Revisa build.gradle.kts del proyecto.
6. Revisa build.gradle.kts del módulo app.
7. Revisa libs.versions.toml si el proyecto utiliza Version Catalog.

NO asumas que el package será `com.example.mini2`.

Utiliza siempre el package real generado por Android Studio.

==================================================
RESTRICCIONES
==================================================

Mantén el proyecto sencillo y acorde al nivel de las prácticas proporcionadas.

NO agregues tecnologías que no son necesarias.

No utilices:

- Firebase.
- Retrofit.
- Hilt.
- Dagger.
- APIs externas.
- Servicios web.
- Autenticación.
- Backend.
- Navigation Compose si no es realmente necesario.
- Clean Architecture excesivamente compleja.
- Librerías externas innecesarias.

No conviertas el proyecto en una aplicación demasiado avanzada.

Debe seguir siendo reconocible como un proyecto académico basado en las prácticas proporcionadas.

Prioriza componentes vistos en los PDF:

- @Composable.
- Scaffold.
- TopAppBar.
- DropdownMenu.
- LazyColumn.
- Card.
- Row.
- Column.
- Button.
- IconButton.
- FloatingActionButton si resulta adecuado.
- OutlinedTextField.
- Checkbox.
- MaterialTheme.
- remember.
- mutableStateOf.
- rememberCoroutineScope.
- Flow.
- collectAsState.
- Room.
- Corrutinas.

==================================================
ESTRUCTURA DEL PROYECTO
==================================================

Crea una estructura organizada siguiendo el estilo utilizado en las prácticas.

Utiliza como mínimo:

<package_principal>
│
├── data
│   ├── Tarea.kt
│   └── TareaDao.kt
│
├── domain
│   └── AppDatabase.kt
│
├── presentation
│   └── MainScreen.kt
│
├── ui.theme
│   └── archivos existentes del tema
│
└── MainActivity.kt

Puedes crear archivos adicionales dentro de `presentation` si ayudan a evitar que MainScreen.kt sea excesivamente grande.

Por ejemplo:

presentation
├── MainScreen.kt
├── TareaItem.kt
└── DialogoTarea.kt

Pero mantén la arquitectura sencilla.

NO introduzcas Repository o ViewModel salvo que resulte estrictamente necesario para hacer funcionar correctamente el proyecto.

Preferiblemente conserva el enfoque directo utilizado en las prácticas.

==================================================
ROOM DATABASE
==================================================

La aplicación debe utilizar Room Database para guardar las tareas permanentemente.

Configura correctamente:

- Room Runtime.
- Room KTX.
- Room Compiler.
- KSP.

Antes de agregar KSP:

REVISA LA VERSIÓN REAL DE KOTLIN DEL PROYECTO.

Selecciona una versión de KSP compatible con esa versión.

NO copies ciegamente las versiones de KSP mostradas en los PDF si son incompatibles con el proyecto actual.

No actualices Kotlin, Gradle, Android Gradle Plugin o Compose salvo que sea estrictamente necesario.

Después de modificar Gradle:

- sincroniza el proyecto;
- compila;
- corrige incompatibilidades.

==================================================
ENTIDAD TAREA
==================================================

Crea:

data/Tarea.kt

Debe representar una tabla Room para almacenar las tareas.

Utiliza una estructura sencilla como:

id
titulo
descripcion
completada
fechaCreacion

Características:

id:
- Int.
- PrimaryKey.
- autoGenerate = true.

titulo:
- String.
- Obligatorio.

descripcion:
- String.
- Puede estar vacía.

completada:
- Boolean.
- Valor inicial false.

fechaCreacion:
- Long.
- Guarda la fecha/hora de creación utilizando System.currentTimeMillis().

Puedes adaptar ligeramente la estructura si existe una razón técnica, pero NO elimines la capacidad de:

- identificar una tarea;
- mostrar su nombre;
- marcarla como completada;
- ordenar por fecha;
- editarla.

==================================================
DAO
==================================================

Crea:

data/TareaDao.kt

Debe utilizar:

@Dao

Debe contener las operaciones necesarias para:

- obtener las tareas;
- insertar;
- actualizar;
- eliminar;
- eliminar completadas.

Utiliza operaciones Room como:

@Query
@Insert
@Update
@Delete

Las operaciones de escritura deben ser:

suspend

La lectura debe utilizar:

Flow<List<Tarea>>

Debe existir al menos una consulta base equivalente a:

SELECT * FROM tareas

Implementa también las consultas necesarias para soportar:

- orden por fecha;
- orden por estado;
- visualización de completadas;
- visualización de pendientes.

Puedes implementar el filtrado mediante consultas Room o mediante transformación de la lista en Compose, utilizando la opción que mantenga el código más sencillo y comprensible.

==================================================
APPDATABASE
==================================================

Crea:

domain/AppDatabase.kt

Debe:

- heredar de RoomDatabase;
- registrar Tarea como entidad;
- exponer tareaDao();
- utilizar version = 1;
- utilizar Singleton;
- utilizar context.applicationContext;
- utilizar Room.databaseBuilder.

Utiliza como nombre de base de datos:

mini2_database

Evita abrir múltiples instancias de Room.

Sigue el mismo patrón Singleton mostrado en las prácticas.

==================================================
INTERFAZ PRINCIPAL
==================================================

Crea:

presentation/MainScreen.kt

La pantalla principal debe utilizar:

Scaffold

Debe contener:

- TopAppBar.
- Menú de opciones.
- Listado de tareas.
- Acción para agregar tareas.

La aplicación debe tener una apariencia sencilla, limpia y basada en Material 3.

No agregues diseños complejos innecesarios.

==================================================
TOPAPPBAR / TOOLBAR
==================================================

La parte superior debe mostrar un TopAppBar.

Título sugerido:

Administrador de Tareas

Agrega un menú Overflow utilizando:

Icons.Default.MoreVert

y:

DropdownMenu

El menú debe permitir interactuar con las tareas.

Debe tener como mínimo opciones equivalentes a:

- Todas.
- Pendientes.
- Completadas.
- Ordenar por fecha.
- Ordenar por estado.
- Borrar completadas.

Puedes organizar algunas de estas opciones en un solo menú para evitar saturar la interfaz.

El estado del menú debe manejarse utilizando:

remember
mutableStateOf

Cuando el usuario seleccione una opción:

- cerrar el DropdownMenu;
- aplicar la acción seleccionada.

==================================================
LISTADO DINÁMICO
==================================================

Utiliza:

LazyColumn

No utilices RecyclerView XML.

Cada elemento debe representar una tarea almacenada en Room.

Utiliza:

Card

para cada tarea.

Dentro de la Card utiliza una estructura similar a:

Row
├── Checkbox
├── Column
│   ├── título
│   └── descripción
└── acciones

Cada Card debe mostrar como mínimo:

- título de la tarea;
- descripción si existe;
- estado completada/pendiente;
- controles necesarios para editar/eliminar.

==================================================
MARCAR TAREA COMO COMPLETADA
==================================================

Cada tarea debe poder cambiar entre:

Pendiente
Completada

Utiliza preferiblemente un:

Checkbox

Al cambiar el estado:

1. Modifica el valor `completada`.
2. Ejecuta la actualización correspondiente en Room.
3. Utiliza una corrutina.
4. La lista debe actualizarse automáticamente mediante Flow.

No recargues manualmente la pantalla.

==================================================
AGREGAR TAREA
==================================================

Agrega una forma clara de crear tareas.

Preferiblemente utiliza:

FloatingActionButton

con:

Icons.Default.Add

Al presionarlo puede aparecer:

- un AlertDialog;
o
- un formulario sencillo dentro de la pantalla.

Preferiblemente utiliza un AlertDialog para mantener limpia la pantalla principal.

El formulario debe solicitar:

Título

y opcionalmente:

Descripción

No permitas guardar una tarea cuyo título esté vacío.

Al confirmar:

- insertar la tarea utilizando Room;
- cerrar el diálogo;
- limpiar los campos.

==================================================
EDITAR TAREA
==================================================

Cada tarea debe tener una opción para editarla.

Puedes utilizar:

Icons.Default.Edit

Al presionar editar:

- abrir un AlertDialog;
- cargar el título actual;
- cargar la descripción actual;
- permitir modificarlos.

Al guardar:

- mantener el mismo id;
- mantener correctamente su estado;
- actualizar utilizando @Update;
- cerrar el diálogo.

No crees una tarea nueva al editar una existente.

==================================================
ELIMINAR TAREA
==================================================

Cada tarea debe permitir eliminarse individualmente.

Puedes utilizar:

Icons.Default.Delete

Al presionarlo:

- ejecutar la eliminación mediante el DAO;
- hacerlo dentro de una corrutina;
- actualizar automáticamente LazyColumn mediante Room + Flow.

Si consideras conveniente, puedes mostrar un AlertDialog sencillo para confirmar la eliminación.

No es obligatorio añadir confirmación si complica innecesariamente la práctica.

==================================================
BORRAR COMPLETADAS
==================================================

En el menú de la TopAppBar debe existir:

Borrar completadas

Esta opción debe eliminar únicamente las tareas donde:

completada = true

Implementa una consulta Room adecuada.

Ejemplo conceptual:

DELETE FROM tareas WHERE completada = 1

La operación debe ejecutarse mediante una corrutina.

==================================================
FILTROS
==================================================

El usuario debe poder visualizar:

- Todas.
- Pendientes.
- Completadas.

Mantén una variable de estado que indique el filtro actual.

Por ejemplo conceptualmente:

TODAS
PENDIENTES
COMPLETADAS

No es necesario crear una arquitectura compleja para esto.

El filtro puede controlarse desde el menú de la TopAppBar.

==================================================
ORDENAMIENTO
==================================================

Implementa opciones para ordenar las tareas.

Como mínimo:

- por fecha;
- por estado.

"Por fecha" debe utilizar el campo fechaCreacion.

"Por estado" debe agrupar u ordenar las tareas de acuerdo con completada/pendiente.

No es necesario implementar algoritmos complejos.

==================================================
FLOW Y ACTUALIZACIÓN REACTIVA
==================================================

La lista debe obtenerse mediante:

Flow<List<Tarea>>

En Compose utiliza:

collectAsState()

o la alternativa equivalente compatible con las dependencias actuales del proyecto.

Cuando ocurra cualquiera de estas operaciones:

- insertar;
- editar;
- completar;
- desmarcar;
- eliminar;
- borrar completadas;

Room debe emitir automáticamente la nueva información y Compose debe actualizar LazyColumn.

NO mantengas una copia independiente innecesaria de la base de datos.

==================================================
CORRUTINAS
==================================================

Utiliza:

rememberCoroutineScope()

y:

coroutineScope.launch

para operaciones de escritura.

Utiliza Dispatchers.IO cuando resulte apropiado.

No bloquees el hilo principal realizando operaciones de Room directamente.

==================================================
MAINACTIVITY
==================================================

Mantén MainActivity.kt sencillo.

Dentro de:

setContent {
}

debe cargar la pantalla principal:

MainScreen()

Conserva el Theme generado por Android Studio si ya existe y utilizarlo es necesario para mantener correctamente Material 3.

No agregues lógica de Room a MainActivity.

No agregues lógica de tareas a MainActivity.

==================================================
MATERIAL DESIGN
==================================================

Utiliza Material 3 siguiendo el estilo visto en las prácticas.

Utiliza cuando corresponda:

MaterialTheme
Scaffold
TopAppBar
Card
FloatingActionButton
DropdownMenu
Icon
IconButton
OutlinedTextField
AlertDialog
Checkbox
Button
Text

Si necesitas iconos adicionales, configura las dependencias correspondientes de Material Icons respetando las versiones actuales del proyecto.

==================================================
ESTADOS VACÍOS
==================================================

Si no existen tareas, muestra un mensaje sencillo como:

No hay tareas registradas

Si un filtro no tiene resultados puedes mostrar:

No hay tareas para este filtro

==================================================
PERSISTENCIA
==================================================

Este punto es obligatorio.

Realiza pruebas para verificar:

1. Crear varias tareas.
2. Cerrar la aplicación.
3. Abrirla nuevamente.
4. Confirmar que las tareas siguen almacenadas.
5. Editar una tarea.
6. Confirmar que el cambio permanece.
7. Marcar una tarea como completada.
8. Reiniciar la aplicación.
9. Confirmar que mantiene su estado.
10. Eliminar una tarea.
11. Confirmar que desaparece permanentemente.
12. Borrar completadas.
13. Confirmar que únicamente desaparecen las completadas.

==================================================
PRUEBAS
==================================================

Realiza pruebas integrales de:

- creación;
- edición;
- eliminación;
- completado;
- desmarcado;
- persistencia;
- filtros;
- ordenamiento;
- menú;
- TopAppBar;
- LazyColumn;
- Room.

Si puedes ejecutar Gradle desde el entorno:

- compila el proyecto;
- corrige todos los errores encontrados.

No dejes errores de compilación deliberadamente.

==================================================
COMPATIBILIDAD
==================================================

Antes de introducir una dependencia:

verifica las versiones reales utilizadas por Mini2.

Evita cambiar versiones que ya funcionan.

Especial atención a:

- Kotlin.
- KSP.
- Room.
- Compose.
- Material 3.

Si el PDF utiliza una versión antigua y el proyecto generado por Android Studio utiliza una versión más nueva:

mantén el enfoque de la práctica, pero utiliza una combinación de dependencias compatible con el proyecto real.

==================================================
COSAS QUE NO DEBES HACER
==================================================

NO generes el PDF de entrega.

NO hagas la portada.

NO subas el proyecto a GitHub.

NO crees el repositorio de GitHub.

NO realices la entrega en Moodle.

NO hagas capturas de pantalla para el documento final.

NO realices automáticamente los retos individuales de las prácticas PDF que no formen parte de los requisitos del Miniproyecto 02.

Esos puntos los realizaré yo personalmente.

Tu trabajo termina dejando funcional el proyecto Android `Mini2`.

==================================================
DOCUMENTACIÓN EN EL CÓDIGO
==================================================

Mantén comentarios breves y educativos similares a los utilizados en las prácticas.

No comentes cada línea.

Comenta principalmente:

- entidad Room;
- DAO;
- Singleton de AppDatabase;
- Flow;
- corrutinas;
- filtros;
- menú;
- operaciones importantes.

==================================================
REVISIÓN FINAL
==================================================

Antes de terminar revisa:

1. Que el proyecto continúe llamándose Mini2.
2. Que el package sea el real generado por Android Studio.
3. Que no existan imports rotos.
4. Que no existan dependencias innecesarias.
5. Que KSP sea compatible.
6. Que Room genere correctamente el código.
7. Que la base de datos funcione.
8. Que LazyColumn muestre las tareas.
9. Que se puedan insertar tareas.
10. Que se puedan editar.
11. Que se puedan marcar como completadas.
12. Que se puedan volver a marcar como pendientes.
13. Que se puedan eliminar.
14. Que se puedan borrar las tareas completadas.
15. Que funcionen los filtros.
16. Que funcione el ordenamiento.
17. Que TopAppBar funcione.
18. Que DropdownMenu funcione.
19. Que los datos persistan después de cerrar la aplicación.
20. Que la aplicación compile.

==================================================
ENTREGA DE TU TRABAJO
==================================================

Cuando termines NO te limites a decir que está terminado.

Dame un resumen que incluya:

- estructura final del proyecto;
- archivos creados;
- archivos modificados;
- entidad Room creada;
- métodos implementados en el DAO;
- dependencias agregadas;
- versión de Room utilizada;
- configuración de KSP utilizada;
- modificaciones realizadas en Gradle;
- funcionamiento de los filtros;
- funcionamiento del ordenamiento;
- funcionamiento de edición;
- funcionamiento de eliminación;
- funcionamiento de completado;
- resultado de las pruebas;
- resultado de la compilación;
- errores encontrados durante el desarrollo;
- cómo fueron solucionados;
- cualquier paso manual que todavía deba realizar yo.

También indícame exactamente:

- cómo ejecutar la aplicación;
- qué acciones debo probar manualmente;
- qué pantallas o estados sería conveniente capturar posteriormente para mi documento PDF.

IMPORTANTE:

No realices el documento PDF final ni la entrega académica.

Solamente deja el proyecto `Mini2` completamente funcional y listo para que yo pueda realizar personalmente las capturas, GitHub, documentación y entrega.