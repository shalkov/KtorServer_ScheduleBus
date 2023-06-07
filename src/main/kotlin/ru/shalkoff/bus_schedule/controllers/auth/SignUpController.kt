package ru.shalkoff.bus_schedule.controllers.auth

import io.ktor.server.application.*
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.network.request.RegisterRequest
import ru.shalkoff.bus_schedule.network.response.LoginResponse
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserRole
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.base.InfoResponse

/**
 * Контроллер регистрации
 */
class SignUpController : BaseAuthController(), KoinComponent {

    private val userDao by inject<UsersDao>()
    private val tokensDao by inject<TokensDao>()
    private val jwtHelper by inject<JWTHelper>()
    private val passwordEncryptor by inject<PasswordEncryptor>()

    suspend fun signUp(call: ApplicationCall): BaseResponse {
        return try {
            val registerRequest = call.receive<RegisterRequest>()

            // todo добавить проверок на валидность данных
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

            saveTokensToCookie(tokens, call)
            LoginResponse(
                newUser.id,
                newUser.login,
                newUser.fullName,
                newUser.email,
                newUser.role,
                tokens,
                InfoResponse(
                    State.SUCCESS,
                    "Регистрация прошла успешно"
                )
            )
        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}