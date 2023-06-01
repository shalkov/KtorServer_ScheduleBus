package ru.shalkoff.bus_schedule.network.response

import ru.shalkoff.bus_schedule.auth.TokensModel
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.InfoResponse
import ru.shalkoff.bus_schedule.db.models.UserRole

data class LoginResponse(
    val id: Int,
    val login: String,
    val fullName: String?,
    val email: String,
    val role: UserRole,
    val tokens: TokensModel,
    override val info: InfoResponse
) : BaseResponse