package com.example.auth.response

data class LoginResponse(
    val id: Int,
    val login: String,
    val fullName: String?,
    val email: String,
    val tokens: TokenResponse
)