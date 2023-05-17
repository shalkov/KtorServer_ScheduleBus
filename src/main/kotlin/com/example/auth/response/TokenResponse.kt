package com.example.auth.response

import com.example.base.BaseResponse
import com.example.base.State

/**
 * Модель с токенами, которая отдаётся пользователю
 */
data class TokenResponse(
    override val status: State,
    override val message: String,
    val accessToken: String,
    val refreshToken: String
) : BaseResponse