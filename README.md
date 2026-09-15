# Mi Formación CTMA — App de Gestión de Actividades Formativas

**Programa:** Análisis y Desarrollo de Software (ADSO)  
**Proyecto Integrador:** Mi Formación CTMA  
**Semana 9 de 11:** Capacidades del dispositivo y seguridad.  
**Tecnología Base:** Android / Kotlin / Jetpack Compose  

---

## 📋 Semana 9 — Capacidades del Dispositivo y Seguridad

En esta etapa, la aplicación permite a los **Estudiantes** adjuntar evidencias fotográficas a sus actividades y a los **Instructores** calificar el progreso basándose en dichas evidencias.

### Funcionalidades Implementadas:
- **Login por Roles**: Diferenciación entre Instructor y Estudiante.
- **Evidencia Fotográfica**: 
    - **Photo Picker**: Selección segura de imágenes de la galería sin permisos amplios.
    - **Cámara**: Captura de fotos mediante `FileProvider` con content URI segura.
    - **Gestión**: Visualización, reemplazo y eliminación de evidencias.
- **Notificaciones**: Alertas para instructores sobre nuevas evidencias (contextual y bajo permiso).
- **Seguridad**:
    - Tráfico **HTTPS** obligatorio en producción.
    - Configuración por **Build Variants** (Dev, Stage, Prod).
    - Validación de tamaño (5MB) y tipo MIME.
    - Sin secretos hardcodeados en el código.

---

## 🛠️ Tecnologías Utilizadas (Semana 9)

* **Photo Picker**: `ActivityResultContracts.PickVisualMedia`.
* **Cámara**: `ActivityResultContracts.TakePicture` con `FileProvider`.
* **Persistencia**: **Jetpack DataStore** (JSON) para actividades y evidencias.
* **Carga de Imágenes**: **Coil** para visualización eficiente de URIs.
* **Seguridad**: `buildConfigField` para URLs de API y `networkSecurityConfig`.

---

## 🏛️ Estructura del Proyecto (Actualizada)

```text
com.example.miformacionctma/
│
├── data/
│   ├── ActividadDataStore.kt      # Persistencia de actividades y evidencias
│   ├── SessionDataStore.kt        # Gestión de sesión de usuario (Roles)
│   └── DataStoreUtils.kt          # Singleton de DataStore
│
├── domain/model/
│   ├── Actividad.kt               # Modelo con estados (ESPERA, EN_CURSO, LISTA, MAL)
│   ├── Evidencia.kt               # Metadatos de la evidencia y estados (LOCAL, FALLIDA, etc)
│   └── User.kt                    # Modelo de usuario y Roles
│
├── ui/viewmodel/
│   ├── ActividadViewModel.kt      # Lógica de negocio, filtrado y validación de evidencias
│   └── AuthViewModel.kt           # Orquestador de sesión y login
│
├── util/
│   ├── FileUtils.kt               # Generación de URIs seguras y metadatos
│   └── NotificationHelper.kt      # Alertas para el sistema de calificado
```

---

## ⚠️ Matriz de Riesgos

| Riesgo | Causa | Impacto | Probabilidad | Control | Verificación |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Exposición de URI | Uso de file:// | Acceso no autorizado | Media | FileProvider (content://) | AndroidManifest / Tests |
| Credenciales en Código | Hardcode | Robo de identidad | Alta | BuildConfig / Secrets | Inspección de código |
| Permisos excesivos | WRITE_EXTERNAL | Riesgo privacidad | Media | Photo Picker (no permis) | Manifest |
| Archivo gigante | Sin validación | Crash / Memoria | Alta | Validación < 5MB | Tests Unitarios |
| MIME inválido | Carga de PDF/Doc | Error UI | Media | Filtro image/* | Prueba manual / ViewModel |
| HTTP en Producción | Configuración base | Sniffing datos | Media | networkSecurityConfig | Inspección .xml |
| Fuga de Logs | Loggear URIs/Tokens | Exposición datos | Baja | Proguard / BuildConfig.DEBUG | Inspección logs |
| Pérdida de Evidencia | Fallo de red | Frustración usuario | Alta | Estado LOCAL persistente | Prueba modo avión |

---

## ✅ Casos de Aceptación (Semana 9)

* **CA01 — Imagen válida**: Preview funcional, URI persistida en DataStore.
* **CA02 — Cancelar**: Si se cancela la cámara, la evidencia previa se mantiene.
* **CA03 — Cámara externa**: Uso de `FileProvider` garantizando seguridad.
* **CA04 — Validación**: Rechazo de archivos > 5MB con mensaje de error.
* **CA05 — Reinicio**: La evidencia y su estado sobreviven al cerrar la app.
* **CA06 — Fallo de subida**: Estado "FALLIDA" permite reintento manual.
* **CA07 — Notificaciones**: Solo se solicitan si el usuario intenta usarlas.
* **CA08 — Eliminar**: Se limpia el registro de la actividad al borrar la evidencia.
* **CA09 — prodRelease**: URL de producción con HTTPS activa.
