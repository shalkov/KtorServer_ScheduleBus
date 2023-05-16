package com.example.plugins

import com.example.auth.*
import com.example.auth.request.AuthRequest
import com.example.auth.request.RefreshTokenRequest
import com.example.auth.request.RegisterRequest
import com.example.auth.response.LoginResponse
import com.example.auth.response.TokenResponse
import com.example.db.dao.tokens.TokensDao
import com.example.db.dao.tokens.TokensDaoImpl
import com.example.db.dao.users.UsersDao
import com.example.db.dao.users.UsersDaoImpl
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import java.text.SimpleDateFormat

fun Application.configureRouting() {

    val userDao: UsersDao = UsersDaoImpl()
    val tokensDao: TokensDao = TokensDaoImpl()
    val jwtHelper: JWTHelper = JWTHelperImpl()
    val passwordEncryptor: PasswordEncryptor = PasswordEncryptorImpl()
    val simpleDateFormat = SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm:ss")

    suspend fun addRefreshTokenToStore(
        userId: Int,
        refreshToken: String,
        tokensDao: TokensDao
    ) {
        //логика добавления рефреш-токена в БД
        val refreshExpirationTime = jwtHelper.getTokenExpiration(refreshToken)
        val refreshFormatExpirationTime = simpleDateFormat.format(refreshExpirationTime)
        tokensDao.addToken(
            userId = userId,
            refreshToken = refreshToken,
            expirationTime = refreshFormatExpirationTime
        )
    }

    /**
     * Получить текущее время в нужном формате
     */
    fun getFormatCurrentTime(): String = simpleDateFormat.format((System.currentTimeMillis()))

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    install(Authentication) {
        jwt {
            verifier(jwtHelper.verifier)
            validate { credential ->
                credential.payload.getClaim("userId").asInt()?.let { userId ->
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Токен не валидный")
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Первый бекенд!")
            //todo сделать отображение Swagger спецификации
        }

        post("auth") {
            try {
                val authRequest = call.receive<AuthRequest>()
                val user = userDao.getUserByLogin(authRequest.login)

                if (user != null && passwordEncryptor.validatePassword(authRequest.password, user.password)) {
                    val tokens = jwtHelper.createTokens(user)
                    addRefreshTokenToStore(user.id, tokens.refreshToken, tokensDao)

                    val authResponse = LoginResponse(
                        user.id,
                        user.login,
                        user.fullName,
                        user.email,
                        tokens
                    )
                    call.respond(authResponse)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Неверный логин или пароль!")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Ошибка!!!")
            }
        }

        post("register") {
            try {
                val registerRequest = call.receive<RegisterRequest>()
                // todo добавить проверок на валидность данных
                // todo добавить проверку, чтобы нельзя было регистрировать один и тот же логин

                val isNotUserExists = userDao.getUserByLogin(registerRequest.login) == null
                if (isNotUserExists) {
                    val user = userDao.addNewUser(
                        registerRequest.login,
                        passwordEncryptor.encryptPassword(registerRequest.password),
                        registerRequest.fullName,
                        registerRequest.email
                    )
                    if (user != null) {
                        val tokens = jwtHelper.createTokens(user)
                        addRefreshTokenToStore(user.id, tokens.refreshToken, tokensDao)

                        val registerResponse = LoginResponse(
                            user.id,
                            user.login,
                            user.fullName,
                            user.email,
                            tokens
                        )
                        call.respond(registerResponse)
                    } else {
                        call.respondText("Ошибка регистрации")
                    }
                } else {
                    call.respondText("Пользователь с таким логином уже существует")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Ошибка!!!")
            }
        }

        post("refresh") {
            try {
                val refreshTokenRequest = call.receive<RefreshTokenRequest>()
                val isRefreshToken = jwtHelper.isRefreshToken(refreshTokenRequest.refreshToken)
                // 1. Проверяем, что рефреш-токен валидный (тут же есть проверка и на срок его жизни)
                // Если всё хорошо, то получим id пользователя
                val userId = jwtHelper.verifyToken(refreshTokenRequest.refreshToken)

                if (isRefreshToken && userId != null) {
                    // 2. Проверяем, есть ли в БД этот рефреш-токен
                    val isRefreshTokenExists = tokensDao.exists(userId, refreshTokenRequest.refreshToken)
                    // 3. В БД находим пользователя по его id, который пришёл в рефреш-токене
                    val user = userDao.getUserById(userId)
                    if (isRefreshTokenExists && user != null) {
                        // 4. Удаляем отправленный в запросе рефреш-токен из БД.
                        // Чтобы его нельзя было использовать повторно, даже если он будет валидным по версии jwtHelper
                        tokensDao.delete(refreshTokenRequest.refreshToken)
                        // 5. Заодно, чтобы не засорять БД, удалим у пользователя все рефреш-токены, у которых истёк срок действия.
                        // Такое может быть, так как для одного пользователя, может быть сгенерировано множество пар токенов.
                        // (в текущей реализации не предусмотрено ограничение, на одну активную сессию для пользователя)
                        tokensDao.deleteAllExpiredByUserId(user.id, getFormatCurrentTime())
                        // 6. Генерируем новую пару токенов для пользователя
                        val tokens = jwtHelper.createTokens(user)
                        // 7. Сохраняем рефреш-токен в БД
                        addRefreshTokenToStore(user.id, tokens.refreshToken, tokensDao)

                        val tokensResponse = TokenResponse(
                            tokens.accessToken,
                            tokens.refreshToken
                        )
                        call.respond(tokensResponse)
                    } else {
                        call.respondText("Ошибка, рефреш-токена нет БД или такой пользователь не существует")
                    }
                } else {
                    call.respondText("Ошибка, при выполнении запроса /refresh. Рефреш-токен не валидный (или истёк его срок действия)")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Ошибка!!!")
            }
        }

        authenticate {
            // Авторизованная зона
            get("/auth_zone") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())

                val user = userDao.getUserById(userId ?: 0)
                call.respondText("Привет, ${user?.login}! Токен протухнет через: $expiresAt ms.")
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
