# Especificación — Práctica 14
Version: 2
Fuente y autorización: petición del usuario de cumplir el PDF íntegro en el proyecto actual y celular físico.
FR-001: Compose y MapaOSMScreen con OSMdroid 6.1.18, OpenTopo, zoom 17 y centro inicial (25.7905,-108.9875).
FR-002: INTERNET, ACCESS_NETWORK_STATE, FINE y COARSE; solicitud en ejecución y seguimiento GPS.
FR-003: vector rojo convertido a Bitmap para setPersonIcon y setDirectionArrow.
FR-004: MarcadorEntity con ID autogenerado, latitud/longitud Double y título String; DAO suspend y Flow; AppDatabase singleton.
FR-005: pulsación larga guarda en Room y Flow actualiza Marker; persistencia tras reiniciar.
FR-006: ejecutar en celular físico y preparar video del reto además del código.
NFR-001: respetar Kotlin 2.2.10, Compose BOM 2026.02.01 y Room 2.8.4 autorizado; conservar configuración SDK del proyecto salvo incompatibilidad comprobada.

Cambio v2: usuario autorizó expresamente Room 2.8.4 para resolver incompatibilidad de metadatos.
