# Hallazgos
- Proyecto plantilla válido: com.example.peluqueria, Compose, AGP 9.3.3, Gradle 9.5.0, Kotlin 2.2.10, SDK 37, minSdk 24.
- SDK /home/simi/Android/Sdk y JDK 25 de Gradle ya instalados. No reinstalar.
- ADB requiere salir del sandbox para abrir su socket. Comprobación autorizada: moto g53 5G, ZY22J2DMXR, estado device.
- Referencias disponibles en PNG de 1536 × 1024; se usarán regiones fotográficas mediante BitmapPainter sin servicios externos.
- Compilación base y de implementación completas con `--offline`; no fue necesario cambiar Gradle, SDK o dependencias.
- Teléfono Android 14/API 34, pantalla 720×1600. ADB instaló APK y am start informó Status: ok, inicio en frío en 1877 ms.
- Proceso inicial 30514 y actividad principal en primer plano; buffer crash filtrado a ese proceso vacío.
- Se revisó solo la pantalla de bienvenida, no se automatizó navegación. Ajustado encuadre para evitar zoom excesivo de la fotografía.
- Atlas de fotografías compartidos para evitar decodificar una imagen completa por tarjeta.
