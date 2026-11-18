# FreshCut
Version: 1

Alcance autorizado: solicitud del usuario del 8 de noviembre de 2026 y dos referencias visuales.

- FR-001: Interfaz española de bienvenida, inicio de sesión, registro y recuperación simulados.
- FR-002: Inicio, búsqueda, catálogo por categorías, detalle con ejemplos y favoritos.
- FR-003: Publicaciones populares con interacción local y perfiles de barberos.
- FR-004: Reserva en tres pasos (servicio, fecha/hora, confirmación), próximas citas, historial, reprogramación y cancelación local.
- FR-005: Perfil y destinos navegables para opciones complementarias.
- NFR-001: Reutilizar proyecto Kotlin/Compose, SDK y dependencias existentes; no backend.
- NFR-002: Apariencia basada en referencias: azul #0F2D5C, rojo #C62828, superficies claras, fotografías, tarjetas redondeadas.
- NFR-003: Sin operaciones Git, sin pruebas automatizadas/instrumentadas, sin cambios fuera del proyecto.
- AC-001: Compilar assembleDebug y, si ADB está autorizado, instalar y abrir en teléfono.

Decisiones: estado local persistente para perfil, sesión, favoritos y reservas; acceso de demostración explícito; fotografías tomadas de regiones de las referencias aportadas y empaquetadas sin red. Los pagos y avisos se presentan como demostración.
