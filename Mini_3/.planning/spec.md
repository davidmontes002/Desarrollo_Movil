# Especificación — Mini_3
Version: 1
La consigna adjunta del usuario es la especificación autorizada. Incluye alcance, arquitectura, entorno, restricciones y criterios verificables; no necesita ampliar requisitos. Se conservan sus decisiones y autorización de ejecución autónoma.
- FR-01: Kotlin, Compose Material 3, MVVM, dos vistas: registro y listado.
- FR-02: Publicacion (id Long autogenerado, titulo, descripcion, fotoUri, fecha Long), Room, DAO suspend/Flow y orden descendente.
- FR-03: Validar título, descripción y foto; fecha automática al guardar; confirmación y limpieza.
- FR-04: TakePicture/FileProvider y PickVisualMedia sin permisos generales; fotos privadas persistentes y limpieza temporal.
- FR-05: LazyColumn con fotografía, título, descripción, fecha y estado vacío.
- NFR-01: Conservar namespace/configuración; sin Git, servicios remotos ni funciones adicionales; escribir sólo en Mini_3.
- AC-01: Resolver Gradle, compilar Debug y revisar integraciones; pruebas físicas a cargo del usuario.
