package com.example.miformacionctma.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val username: String? = null,
    val rol: String? = "STUDENT"
)
