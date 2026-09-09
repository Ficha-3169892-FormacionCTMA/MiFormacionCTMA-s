import { describe, it, expect } from 'vitest'
import { esActividadCritica, calcularPromedio } from './actividades.logic'

describe('Lógica de Actividades (Unit Tests)', () => {

  it('debería marcar como crítica una actividad ALTA con 10% de progreso', () => {
    const act = { prioridad: 'ALTA', progreso: 10 }
    expect(esActividadCritica(act)).toBe(true)
  })

  it('no debería marcar como crítica una actividad ALTA con 80% de progreso', () => {
    const act = { prioridad: 'ALTA', progreso: 80 }
    expect(esActividadCritica(act)).toBe(false)
  })

  it('debería calcular el promedio correctamente', () => {
    const lista = [{ progreso: 50 }, { progreso: 100 }]
    expect(calcularPromedio(lista)).toBe(75)
  })

  it('debería devolver 0 si la lista de actividades está vacía', () => {
    expect(calcularPromedio([])).toBe(0)
  })
})
