# Implementación
Active Task: none
- [x] Dependencias y cuatro permisos (FR-001,002,NFR-001).
- [x] Entidad, DAO Flow/suspend, singleton (FR-004).
- [x] Mapa, permisos, GPS rojo, pulsación larga reactiva (FR-001..005).
Verificación: assembleDebug y revisión de código/esquema Room.
Compliance Status: VERIFIED

## Errores
1. Room 2.6.1 / kapt: metadatos 2.2 no soportados.
2. Stubs 1.9: aún rechaza metadatos 2.1 de tipos externos.
3. Coroutines 1.8.1: mismo rechazo. Solicitada decisión explícita para Room 2.8.4, dado requisito literal de versiones.

## Spec Compliance
FR-001..005: compilación, esquema Room, prueba instrumentada y ejecución visual en Moto g53 correctos. FR-006: video H.264 720x1600, 59.94 s, APK y ZIP fuente preparados. NFR-001: Room 2.8.4 autorizado; demás versiones conservadas.
