package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.data.repository.AuthRepository
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object Unauthenticated : AuthUiState
    data class Authenticated(val user: User) : AuthUiState
    data object RegistrationSuccess : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.value = AuthUiState.Authenticated(user)
            } else {
                _uiState.value = AuthUiState.Unauthenticated
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = repository.signIn(email, pass)
                _uiState.value = AuthUiState.Authenticated(user)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(email: String, pass: String, username: String, role: Role) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.signUp(email, pass, username, role)
                _uiState.value = AuthUiState.RegistrationSuccess
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error al registrarse")
            }
        }
    }

    fun resetAuthState() {
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun logout() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
