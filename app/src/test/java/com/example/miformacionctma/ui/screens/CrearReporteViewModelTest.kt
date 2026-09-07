package com.example.miformacionctma.ui.screens

import com.example.miformacionctma.domain.Reporte
import com.example.miformacionctma.domain.ReporteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CrearReporteViewModelTest {

    private lateinit var viewModel: CrearReporteViewModel
    private lateinit var repository: FakeReporteRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeReporteRepository()
        viewModel = CrearReporteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- PRUEBAS HU-4: MEJORAR PERSISTENCIA DEL ESTADO DE LAS ACTIVIDADES ---

    @Test
    fun `HU-4 CP-11 - Carga de datos - El ViewModel inicializa correctamente`() {
        assertNotNull(viewModel.uiState.value)
        assertEquals("", viewModel.uiState.value.titulo)
    }

    // --- PRUEBAS HU-2: MEJORAR VALIDACIÓN DEL FORMULARIO ---

    @Test
    fun `HU-2 CP-12 - Corrección de errores - Actualización reactiva del título`() {
        viewModel.actualizarTitulo("Nuevo Reporte")
        assertEquals("Nuevo Reporte", viewModel.uiState.value.titulo)
        assertNull(viewModel.uiState.value.errorTitulo)
    }

    @Test
    fun `HU-2 CP-04 - Título con longitud superior al máximo - Límite de 80 caracteres`() {
        val tituloLargo = "a".repeat(81)
        viewModel.actualizarTitulo(tituloLargo)
        assertNotEquals(tituloLargo, viewModel.uiState.value.titulo)
    }

    @Test
    fun `HU-2 CP-02 - Título con longitud inferior al mínimo - Fallo al guardar título corto`() {
        viewModel.actualizarTitulo("abc")
        viewModel.guardar()
        
        assertNotNull(viewModel.uiState.value.errorTitulo)
        assertEquals(0, repository.reportesList.size)
    }

    // --- PRUEBAS HU-1: MEJORAR DISEÑO VISUAL DE LAS PANTALLAS ---

    @Test
    fun `HU-1 CP-03 - Creación de actividad - Guardado exitoso de reporte`() {
        val tituloValido = "Reporte Válido"
        viewModel.actualizarTitulo(tituloValido)
        viewModel.guardar()
        
        assertNull(viewModel.uiState.value.errorTitulo)
        assertEquals(1, repository.reportesList.size)
        assertEquals(tituloValido, repository.reportesList[0].titulo)
        assertNotNull(viewModel.uiState.value.guardadoId)
    }

    class FakeReporteRepository : ReporteRepository {
        val reportesList = mutableListOf<Reporte>()
        override val reportes: StateFlow<List<Reporte>> = MutableStateFlow(reportesList)
        
        override fun agregar(reporte: Reporte) {
            reportesList.add(reporte)
        }
    }
}
