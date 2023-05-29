package ru.shalkoff.bus_schedule.auth

data class TokensModel(
    val accessToken: String,
    val refreshToken: String
)