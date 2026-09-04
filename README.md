# Mi Formación CTMA — App de Gestión de Actividades Formativas

**Programa:** Análisis y Desarrollo de Software (ADSO)  
**Proyecto Integrador:** Mi Formación CTMA / Caso Integrador EntregaSegura  
**Semanas:** 2, 3 y 4 de 11  
**Tecnología Base:** Android / Kotlin / Jetpack Compose  

---

## 📋 Descripción del Proyecto

**Mi Formación CTMA** es  una aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose** orientada a la gestión, registro, seguimiento y validación de actividades formativas. 

El proyecto integra los principios de planificación de pruebas y aseguramiento de calidad del caso **EntregaSegura** (Semanas 2 y 3) junto con la arquitectura móvil multipantalla implementada en **Jetpack Compose** (Semana 4). La aplicación permite la creación de actividades, validación rigurosa de formularios, consulta de detalles técnicos, navegación fluida entre pantallas y un manejo eficiente del estado inmutable.

---

## 🛠️ Tecnologías y Conceptos Clave

* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Arquitectura & Navegación:** Navigation Compose (rutas tipadas, paso de argumentos por ID, gestión de Back Stack).
* **Gestión de Estado:** 
    * `remember` y `rememberSaveable` (persistencia del borrador ante recreación de Activity).
    * **State Hoisting**: Separación de lógica y presentación.
    * **UI State Inmutable**: Uso de Data Classes (`FormularioActividadUiState`) para representar el estado de la interfaz.
* **Patrón de Arquitectura:** Unidirectional Data Flow (UDF).
* **Validación de Datos:** Reglas de negocio aplicadas a títulos, descripciones, fechas y progreso.
* **Pruebas Unitarias:** Implementación de JUnit 4 y Kotlinx Coroutines Test para validación de lógica de negocio y ViewModels.
* **Construcción:** Gradle (KTS) / Version Catalogs (`libs.versions.toml`).

---

## 🏛️ Estructura del Proyecto

```text
com.example.miformacionctma/
│
├── domain/
│   ├── ActividadFormativa.kt      # Modelo de datos con soporte para fechas
│   └── ReglasActividad.kt         # Lógica de validación (Título, Desc, Fecha, Progreso)
│
├── ui/
│   ├── components/
│   │   ├── ResumenActividades.kt  # Resumen visual de estadísticas
│   │   ├── TarjetaActividad.kt    # Tarjeta interactiva con indicadores de progreso
│   │   └── FormularioActividad.kt # Composable stateless para captura de datos
│   │
│   ├── screens/
│   │   ├── PantallaActividades.kt       # Listado principal con FAB de navegación
│   │   ├── FormularioActividadUiState.kt# Modelo inmutable del estado del formulario
│   │   ├── PantallaCrearActividad.kt    # State Holder con lógica de validación y guardado
│   │   ├── PantallaDetalleActividad.kt  # Visualización detallada por ID
│   │   ├── CrearReporteViewModel.kt     # Lógica avanzada con ViewModel y StateFlow
│   │   └── PantallaReporte.kt           # Nueva pantalla de reportes (stateless content + route)
│   │
│   └── theme/
│       ├── Color.kt, Theme.kt, Type.kt  # Configuración de Material Design 3
│
├── DatosEjemplo.kt                # Fuente de datos iniciales persistente en sesión
└── MainActivity.kt                # Orquestador de navegación (NavHost) y Estado Global

[Test Root]
├── domain/
│   └── ReglasActividadTest.kt      # Pruebas unitarias para validación de actividades
└── ui/screens/
    └── CrearReporteViewModelTest.kt# Pruebas unitarias para el ViewModel de reportes
```

---

## ✅ Objetivos de la Semana 4 Cumplidos

1.  **Navegación Robusta**: Flujo completo Lista → Crear → Lista y Lista → Detalle → Lista.
2.  **Validación en Tiempo Real**: Mensajes de error visuales y deshabilitación del botón de guardado ante datos inválidos.
3.  **Persistencia del Borrador**: Los datos del formulario se mantienen al rotar la pantalla gracias a `rememberSaveable`.
4.  **Protección de Datos**: Prevención de duplicados mediante control de estado en el botón de guardado.
5.  **Arquitectura Limpia**: Implementación de State Hoisting y separación de responsabilidades.
6.  **Calidad Asegurada**: Cobertura de pruebas unitarias para las reglas de negocio críticas y el flujo de estado de los componentes.

## 📚 Evidencias del proyecto

### Semana 2

- [📄 Entrega Semana 2 - Pruebas y Planificación](docs/evidencias/Entrega_Semana_2_Pruebas_y_Planificacion.docx)

### Semana 3

- [📄 Entrega Semana 3 - Casos, Ejecución y Defectos](docs/evidencias/Entrega_Semana_3_Casos_Ejecucion_y_Defectos.docx)