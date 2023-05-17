package com.example.auth.response

import com.example.db.models.UserRole

data class LoginResponse(
    val id: Int,
    val login: String,
    val fullName: String?,
    val email: String,
    val roles: List<UserRole>,
    val tokens: TokenResponse
)