# Hallazgos
- Proyecto real /home/simi/AndroidStudioProjects/Mini2; nombre Mini2; namespace/applicationId com.example.mini2. Plantilla sin cambios de usuario en fuentes.
- Kotlin integrado en AGP 2.2.10, AGP 9.3.3, Gradle 9.5.0, plugin Compose 2.2.10, BOM 2026.02.01, minSdk 24, target/compileSdk 37.
- Se reutiliza combinación verificada en esta sesión: Room 2.8.4 + KSP2 2.3.4. Fuentes oficiales consultadas: https://github.com/google/ksp/releases/tag/2.3.0 y https://github.com/google/ksp/releases/tag/2.3.1 y https://developer.android.com/jetpack/androidx/releases/room.
- Práctica 06: Card/Row/Column y LazyColumn/items. Práctica 07: Scaffold con innerPadding, TopAppBar y DropdownMenu con remember/mutableStateOf. Práctica 08: entidad, DAO, singleton, Flow y corrutinas.
- Los retos individuales de los PDF quedan fuera; solo se implementan requisitos de Mini2. Se conservan archivos generados del tema.
- Filtrado y orden derivados con funciones de listas Kotlin. Fecha: descendente con id como desempate; estado: pendientes primero y fecha descendente dentro de cada grupo.
- Las escrituras se serializan desde la UI deshabilitando controles durante cada operación; errores de SQLite conservan el formulario para reintentar.
- dependencyInsight confirmó Material 3 1.4.0 antes de modificar fuentes. Primera compilación assembleDebug exitosa en 25 s; KSP generó TareaDao_Impl y AppDatabase_Impl.
- Pruebas de interfaz usan BD en memoria aislada. La prueba por procesos utiliza únicamente dos registros identificados con prefijo de prueba y los retira al finalizar.
