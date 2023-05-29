package ru.shalkoff.bus_schedule.auth.response

import ru.shalkoff.bus_schedule.auth.TokensModel
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.models.UserRole

data class LoginResponse(
    override val status: State,
    override val message: String,
    val id: Int,
    val login: String,
    val fullName: String?,
    val email: String,
    val role: UserRole,
    val tokens: TokensModel
) : BaseResponse