package ru.shalkoff.bus_schedule.auth.request

data class RegisterRequest(
    val login: String,
    val password: String,
    val fullName: String,
    val email: String
)