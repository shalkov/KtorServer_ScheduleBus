package com.example.auth.response

data class RegisterResponse(
    val id: Int,
    val login: String,
    val fullName: String?,
    val email: String,
    val tokens: TokenResponse
)