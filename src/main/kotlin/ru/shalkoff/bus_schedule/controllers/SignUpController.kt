package ru.shalkoff.bus_schedule.controllers

import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.auth.request.RegisterRequest
import ru.shalkoff.bus_schedule.auth.response.LoginResponse
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserRole
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Контроллер регистрации
 */
class SignUpController : BaseAuthController(), KoinComponent {

    private val userDao by inject<UsersDao>()
    private val tokensDao by inject<TokensDao>()
    private val jwtHelper by inject<JWTHelper>()
    private val passwordEncryptor by inject<PasswordEncryptor>()

    suspend fun signUp(registerRequest: RegisterRequest): BaseResponse {
        return try {

            // todo добавить проверок на валидность данных
            // todo добавить проверку, чтобы нельзя было регистрировать один и тот же логин

            val user = userDao.getUserByLogin(registerRequest.login)
            if (user != null) {
                throw BadRequestException("Пользователь с таким логином уже существует")
            }
            val newUser = userDao.addNewUser(
                registerRequest.login,
                passwordEncryptor.encryptPassword(registerRequest.password),
                registerRequest.fullName,
                registerRequest.email,
                UserRole.USER
            ) ?: throw BadRequestException("Не удалось добавить пользователя в БД")

            val tokens = jwtHelper.createTokens(newUser)
            addRefreshTokenToStore(newUser.id, tokens.refreshToken, tokensDao)

            LoginResponse(
                State.SUCCESS,
                "Регистрация прошла успешно",
                newUser.id,
                newUser.login,
                newUser.fullName,
                newUser.email,
                newUser.role,
                tokens
            )
        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}