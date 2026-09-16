package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad

val actividadesEjemplo = listOf(

    ActividadFormativa(
        id = 1L,
        titulo = "Kotlin básico",
        descripcion = "Aprender los fundamentos de Kotlin",
        progreso = 100,
        fecha = "2026-08-17",
        diasRestantes = -2,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 2L,
        titulo = "Variables y tipos",
        descripcion = "Practicar val, var y tipos de datos",
        progreso = 80,
        fecha = "2026-08-20",
        diasRestantes = 1,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 3L,
        titulo = "Funciones en Kotlin",
        descripcion = "Crear y utilizar funciones",
        progreso = 60,
        fecha = "2026-08-22",
        diasRestantes = 3,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 4L,
        titulo = "Null Safety",
        descripcion = "Trabajar con valores nulos de forma segura",
        progreso = 40,
        fecha = "2026-08-24",
        diasRestantes = 5,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 5L,
        titulo = "Data Classes",
        descripcion = "Crear modelos de datos en Kotlin",
        progreso = 20,
        fecha = "2026-08-21",
        diasRestantes = 2,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 6L,
        titulo = "Colecciones",
        descripcion = "Trabajar con listas y filtros",
        progreso = 0,
        fecha = "2026-08-26",
        diasRestantes = 7,
        prioridad = Prioridad.BAJA
    ),

    ActividadFormativa(
        id = 7L,
        titulo = "Jetpack Compose",
        descripcion = "Introducción a interfaces declarativas",
        progreso = 70,
        fecha = "2026-08-23",
        diasRestantes = 4,
        prioridad = Prioridad.ALTA
    ),

    ActividadFormativa(
        id = 8L,
        titulo = "Material 3",
        descripcion = "Utilizar componentes de Material Design 3",
        progreso = 50,
        fecha = "2026-08-25",
        diasRestantes = 6,
        prioridad = Prioridad.MEDIA
    ),

    ActividadFormativa(
        id = 9L,
        titulo = "Accesibilidad",
        descripcion = "Mejorar la experiencia para todos los usuarios",
        progreso = 30,
        fecha = "2026-08-27",
        diasRestantes = 8,
        prioridad = Prioridad.BAJA
    ),

    ActividadFormativa(
        id = 10L,
        titulo = "Proyecto Mi Formación CTMA",
        descripcion = "Integrar los conocimientos aprendidos",
        progreso = 90,
        fecha = "2026-08-21",
        diasRestantes = 2,
        prioridad = Prioridad.ALTA
    )
)