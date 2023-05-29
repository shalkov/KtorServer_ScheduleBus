package ru.shalkoff.bus_schedule.base

interface BaseResponse {
    val status: State
    val message: String
}

enum class State {
    SUCCESS, NOT_FOUND, FAILED, UNAUTHORIZED, SERVER_ERROR
}