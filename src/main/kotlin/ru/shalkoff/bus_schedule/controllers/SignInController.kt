package ru.shalkoff.bus_schedule.controllers

import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.auth.request.AuthRequest
import ru.shalkoff.bus_schedule.auth.response.LoginResponse
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Контроллер авторизации
 */
class SignInController : BaseAuthController(), KoinComponent {

    private val userDao by inject<UsersDao>()
    private val tokensDao by inject<TokensDao>()
    private val jwtHelper by inject<JWTHelper>()
    private val passwordEncryptor by inject<PasswordEncryptor>()

    suspend fun signIn(authRequest: AuthRequest): BaseResponse {
        return try {
            val user = userDao.getUserByLogin(authRequest.login)

            if (user != null && passwordEncryptor.validatePassword(authRequest.password, user.password)) {
                val tokens = jwtHelper.createTokens(user)
                addRefreshTokenToStore(user.id, tokens.refreshToken, tokensDao)

                LoginResponse(
                    status = State.SUCCESS,
                    message = "Авторизация прошла успешно!",
                    user.id,
                    user.login,
                    user.fullName,
                    user.email,
                    user.role,
                    tokens
                )
            } else {
                GeneralResponse.unauthorized("Неверный логин или пароль!")
            }
        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}