import express from 'express'
import cors from 'cors'
import { ActividadesService } from './services/actividades.service'

const app = express()

app.use(cors())
app.use(express.json())

app.get('/health', (req, res) => {
  res.status(200).json({ status: 'OK', message: 'API Mi Formación CTMA funcionando' })
})

app.get('/actividades', async (req, res) => {
  try {
    const lista = await ActividadesService.listar()
    res.status(200).json(lista)
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener actividades' })
  }
})

app.post('/actividades', async (req, res) => {
  const { titulo } = req.body
  if (!titulo) return res.status(400).json({ error: 'Título requerido' })

  try {
    const nueva = await ActividadesService.crear(req.body)
    res.status(201).json(nueva)
  } catch (error) {
    res.status(500).json({ error: 'Error al crear actividad' })
  }
})

export { app }
