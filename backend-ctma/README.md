# Backend Mi Formación CTMA - Semana 8

Este repositorio contiene el API REST para el sistema de gestión de actividades formativas, desarrollado siguiendo las directrices de la **Semana 8 del programa de Análisis y Desarrollo de Software (SENA)**.

## 🚀 Tecnologías y Herramientas
- **Entorno:** Node.js con TypeScript.
- **Framework:** Express.js.
- **Pruebas (QA):** Vitest (Motor de pruebas) y Supertest (Integración de API).
- **Metodología:** Test-Driven Development (TDD) y Shift-left Testing.

## 🛠️ Instalación y Configuración
Asegúrese de tener Node.js instalado en su sistema.

1. Ingrese a la carpeta del proyecto:
   ```bash
   cd backend-ctma
   ```
2. Instale las dependencias:
   ```bash
   npm install
   ```

## 🧪 Estrategia de Pruebas Automatizadas
Se ha implementado una suite de pruebas completa que garantiza la calidad del software:

### 1. Ejecutar Pruebas
Para correr todas las pruebas unitarias y de integración:
```bash
npm test
```

### 2. Reporte de Cobertura (Code Coverage)
Para generar el reporte de qué porcentaje de código está cubierto por pruebas:
```bash
npm run test:coverage
```
*El reporte detallado se genera en la carpeta `/coverage` en formato HTML.*

## 📌 Endpoints del API
| Método | Ruta | Propósito | Respuesta Exitosa |
| :--- | :--- | :--- | :--- |
| GET | `/health` | Verificar estado del servidor | 200 OK |
| GET | `/actividades` | Listar actividades formativas | 200 OK (Array) |
| POST | `/actividades` | Crear una nueva actividad | 201 Created (Objeto) |

## 📐 Evidencia Metodológica (Scrum)
### Definition of Done (DoD)
- Código 100% en TypeScript sin errores de Linting.
- Suite de pruebas automatizada con Vitest.
- Cobertura de código mínima del 80%.
- Uso de Mocks para aislamiento de dependencias externas.

### Ciclo TDD Aplicado
Se siguió rigurosamente el flujo **Red -> Green -> Refactor**:
1. **Red**: Creación de pruebas esperando fallos en endpoints inexistentes.
2. **Green**: Implementación mínima para satisfacer los requisitos del test.
3. **Refactor**: Mejora de la arquitectura separando la lógica en Capas de Servicio y Controladores.

---
**Desarrollado por:** Estudiante de Análisis y Desarrollo de Software - SENA
**Fecha:** Septiembre 2026
