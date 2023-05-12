package com.example.auth.response

data class RegisterResponse(
    val id: Int,
    val login: String,
    val fullName: String?,
    val tokens: TokenResponse
)