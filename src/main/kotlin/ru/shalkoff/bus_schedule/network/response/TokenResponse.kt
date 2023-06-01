package ru.shalkoff.bus_schedule.network.response

import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.InfoResponse

/**
 * Модель с токенами, которая отдаётся пользователю
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    override val info: InfoResponse
) : BaseResponse