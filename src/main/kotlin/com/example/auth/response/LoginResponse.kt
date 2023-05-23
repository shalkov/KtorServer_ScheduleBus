package com.example.auth.response

import com.example.auth.TokensModel
import com.example.base.BaseResponse
import com.example.base.State
import com.example.db.models.UserRole
import io.ktor.http.cio.*

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