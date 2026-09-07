package com.example.miformacionctma.ui.viewmodel

import com.example.miformacionctma.data.ActividadDataSource
import com.example.miformacionctma.data.SortOrder
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.state.ListadoUiState
import com.example.miformacionctma.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ActividadesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ActividadesViewModel
    private lateinit var fakeDataSource: FakeActividadDataSource

    @Before
    fun setup() {
        fakeDataSource = FakeActividadDataSource()
    }

    @Test
    fun `HU-7 CP-01 - Estado inicial es Cargando antes de recibir datos`() = runTest {
        viewModel = ActividadesViewModel(fakeDataSource)
        val estadoActual = viewModel.uiState.value
        assertEquals(ListadoUiState.Cargando, estadoActual)
    }

    @Test
    fun `HU-7 CP-02 - Cuando DataStore emite lista vacia, el estado es Vacio`() = runTest {
        fakeDataSource.emit(emptyList())
        viewModel = ActividadesViewModel(fakeDataSource)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        
        assertEquals(ListadoUiState.Vacio, viewModel.uiState.value)
        job.cancel()
    }

    @Test
    fun `HU-7 CP-03 - Cuando hay datos, el estado es Contenido con la lista correcta`() = runTest {
        val lista = listOf(
            ActividadFormativa(1L, "Test", null, 0, "2024-09-07", 0, Prioridad.MEDIA)
        )
        fakeDataSource.emit(lista)
        viewModel = ActividadesViewModel(fakeDataSource)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value
        assertTrue("El estado debería ser Contenido", state is ListadoUiState.Contenido)
        job.cancel()
    }

    class FakeActividadDataSource : ActividadDataSource {
        private val _flow = MutableStateFlow<List<ActividadFormativa>>(emptyList())
        override val actividadesFlow: Flow<List<ActividadFormativa>> = _flow
        
        private val _sortFlow = MutableStateFlow(SortOrder.FECHA)
        override val sortOrderFlow: Flow<SortOrder> = _sortFlow

        fun emit(lista: List<ActividadFormativa>) {
            _flow.value = lista
        }

        override suspend fun guardarActividades(actividades: List<ActividadFormativa>) {
            _flow.value = actividades
        }

        override suspend fun updateSortOrder(order: SortOrder) {
            _sortFlow.value = order
        }
    }
}
