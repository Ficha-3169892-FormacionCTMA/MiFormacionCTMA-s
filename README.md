# Mi Formación CTMA - Semana 4

## Objetivo
Implementar estado, formularios, validación y navegación en la aplicación.

## Cambios Principales
- **Estado y State Hoisting**: Se implementó `FormularioActividadUiState` para manejar el estado del formulario de forma inmutable. El composable `FormularioActividad` es stateless y recibe eventos del contenedor `PantallaFormularioActividad`.
- **Navegación**: Se integró `Navigation Compose` con tres destinos principales:
    - `lista`: Pantalla principal con el listado de actividades.
    - `crear`: Formulario para agregar nuevas actividades.
    - `detalle/{actividadId}`: Vista detallada de una actividad específica, pasando solo el ID como argumento.
- **Validación**: Se implementaron reglas de negocio en `ReglasActividad`:
    - Título obligatorio (3-80 caracteres).
    - Descripción opcional (máx 240 caracteres).
    - Fecha con formato AAAA-MM-DD (no anterior a hoy).
    - Progreso entre 0 y 100.
- **Persistencia Temporal**: Uso de `rememberSaveable` para mantener los borradores del formulario ante recreaciones de la Activity.
- **Protección**: El botón de guardado se deshabilita tras la primera pulsación exitosa para evitar duplicados.

## Navegación Implementada
- Lista -> FAB -> Crear -> Guardar -> Lista
- Lista -> Tarjeta -> Detalle -> Atrás -> Lista

## Pruebas Realizadas (HU-16)
- **CP-01**: Conservación de datos por rotación de pantalla (Automatizado en `ConservacionEstadoFormularioTest`).
- **CP-03**: Persistencia de mensajes de validación y errores tras recreación (Automatizado).
- **CP-04**: Prevención de duplicación al guardar tras recreación (Automatizado).
- **CP-05**: Retención de tipos de datos complejos y selecciones (Automatizado).
- **Unitarias**: Validación de títulos, fechas y progreso en `ReglasActividadTest`.
