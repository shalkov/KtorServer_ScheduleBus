package ru.shalkoff.bus_schedule.network.request

data class AuthRequest(
    val login: String,
    val password: String
)