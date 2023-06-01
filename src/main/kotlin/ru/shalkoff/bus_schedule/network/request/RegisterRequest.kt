package ru.shalkoff.bus_schedule.network.request

data class RegisterRequest(
    val login: String,
    val password: String,
    val fullName: String,
    val email: String
)