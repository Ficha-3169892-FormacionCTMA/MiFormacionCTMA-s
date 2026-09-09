import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from 'supertest'
import { app } from './app'
import { ActividadesService } from './services/actividades.service'

// 1. Creamos el MOCK del servicio
vi.mock('./services/actividades.service', () => ({
  ActividadesService: {
    listar: vi.fn(),
    crear: vi.fn()
  }
}))

describe('Endpoint de Actividades (con Mocks)', () => {

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('GET /actividades debería devolver datos del Mock', async () => {
    // 2. Definimos qué queremos que devuelva el "falso" servicio
    const datosFalsos = [{ id: 99, titulo: 'Actividad de Prueba', prioridad: 'BAJA' }]
    vi.mocked(ActividadesService.listar).mockResolvedValue(datosFalsos)

    const response = await request(app).get('/actividades')

    expect(response.status).toBe(200)
    expect(response.body).toEqual(datosFalsos)
  })

  it('GET /actividades debería devolver 500 si el servicio falla', async () => {
    // 3. Simulamos un error en la base de datos
    vi.mocked(ActividadesService.listar).mockRejectedValue(new Error('DB Error'))

    const response = await request(app).get('/actividades')

    expect(response.status).toBe(500)
    expect(response.body).toEqual({ error: 'Error al obtener actividades' })
  })
})
