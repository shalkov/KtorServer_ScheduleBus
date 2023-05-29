package ru.shalkoff.bus_schedule.auth.response

import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.State

/**
 * Модель с токенами, которая отдаётся пользователю
 */
data class TokenResponse(
    override val status: State,
    override val message: String,
    val accessToken: String,
    val refreshToken: String
) : BaseResponse