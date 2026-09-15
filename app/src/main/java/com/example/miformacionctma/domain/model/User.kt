package com.example.miformacionctma.domain.model

enum class Role {
    STUDENT,
    INSTRUCTOR
}

data class User(
    val id: String = "",
    val username: String,
    val email: String = "",
    val role: Role = Role.STUDENT
)