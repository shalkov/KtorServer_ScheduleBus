package com.example.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String,
    val fullName: String,
    val email: String
)