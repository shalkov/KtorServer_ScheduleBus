package ru.shalkoff.bus_schedule.auth.request

data class AuthRequest(
    val login: String,
    val password: String
)