package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, role: Role) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Authenticated(User(username, role.name))
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
