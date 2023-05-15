package com.example.db.models

data class Token(
    val userId: Int,
    val refreshToken: String,
    val expiresAt: Long
)