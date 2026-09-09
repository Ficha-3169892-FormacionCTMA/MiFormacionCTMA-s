/**
 * Determina si una actividad requiere atención inmediata.
 * Regla: Prioridad ALTA y progreso menor al 20%.
 */
export function esActividadCritica(actividad: { prioridad: string; progreso: number }): boolean {
  return actividad.prioridad === 'ALTA' && actividad.progreso < 20;
}

/**
 * Calcula el promedio de progreso de una lista de actividades.
 */
export function calcularPromedio(actividades: { progreso: number }[]): number {
  if (actividades.length === 0) return 0;
  const suma = actividades.reduce((acc, act) => acc + actividad.progreso, 0);
  return suma / actividades.length;
}
