package ru.shalkoff.bus_schedule.db.models

data class TokenModel(
    val userId: Int,
    val refreshToken: String,
    val expirationTime: String
)