# Hallazgos

- PDF revisado visualmente: /home/simi/Documentos/Gibran/Practica 08 - SQLite - RoomDatebase.pdf (5 páginas).
- El texto del usuario aclara id autogenerado, inserción suspend y conservación del nombre/paquete; prevalece sobre ejemplos inconsistentes del PDF.
- Namespace com.example.practica_8; nombre practica_8. AGP 9.3.3, Kotlin integrado 2.2.10 (POM de AGP), plugin Compose 2.2.10, BOM 2026.02.01, Gradle 9.5.0, minSdk 24, compile/target 37.
- PDF usa KSP 1.9.24-1.0.20 y 1.9.0-1.0.13 simultáneamente, y Room 2.6.1. Se adapta a KSP 2.3.4 (KSP2 compatible con Kotlin integrado de AGP 9) y Room 2.8.4.
- Fuentes: https://github.com/google/ksp/issues/2615 ; https://github.com/google/ksp/releases/tag/2.3.4 ; https://developer.android.com/jetpack/androidx/releases/room
- No se agrega android.disallowKotlinSourceSets=false salvo que el error aparezca.
- Gradle daemon configurado por el proyecto para Java 25; Java del sistema 27, JBR disponible en /opt/android-studio/jbr.
