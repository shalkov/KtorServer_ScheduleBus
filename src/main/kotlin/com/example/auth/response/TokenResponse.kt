package com.example.auth.response

/**
 * Модель с токенами, которая отдаётся пользователю
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)