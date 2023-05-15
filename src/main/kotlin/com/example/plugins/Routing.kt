package com.example.plugins

import com.example.auth.*
import com.example.auth.request.AuthRequest
import com.example.auth.request.RegisterRequest
import com.example.auth.response.RegisterResponse
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
import io.ktor.server.request.*

fun Application.configureRouting() {

    val userDao: UsersDao = UsersDaoImpl()
    val jwtHelper: JWTHelper = JWTHelperImpl()
    val passwordEncryptor: PasswordEncryptor = PasswordEncryptorImpl()

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
                    val registerResponse = RegisterResponse(
                        user.id,
                        user.login,
                        user.fullName,
                        user.email,
                        tokens
                    )
                    call.respond(registerResponse)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Неверный логин или пароль!")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Ошибка!!!")
            }
        }

        post("register") {
            val registerRequest = call.receive<RegisterRequest>()
            // todo добавить проверок на валидность данных
            // todo добавить проверку, чтобы нельзя было регистрировать один и тот же логин

            val user = userDao.addNewUser(
                registerRequest.login,
                passwordEncryptor.encryptPassword(registerRequest.password),
                registerRequest.fullName,
                registerRequest.email
            )
            if (user != null) {
                val tokens = jwtHelper.createTokens(user)
                val registerResponse = RegisterResponse(
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
        }

        authenticate {
            // Авторизованная зона
            get("/auth_zone") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                val user = userDao.user(userId ?: 0)
                call.respondText("Привет, ${user?.login}! Токен протухнет через: $expiresAt ms.")
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
