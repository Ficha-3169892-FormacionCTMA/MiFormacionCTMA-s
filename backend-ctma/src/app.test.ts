import { describe, it, expect } from 'vitest'
import request from 'supertest'
import { app } from './app'

describe('API Health Check', () => {
  it('debería responder con status 200 y un mensaje de OK', async () => {
    const response = await request(app).get('/health')

    expect(response.status).toBe(200)
    expect(response.body).toEqual({ status: 'OK', message: 'API Mi Formación CTMA funcionando' })
  })
})
