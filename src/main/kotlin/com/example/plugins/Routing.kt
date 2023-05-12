package com.example.plugins

import com.auth0.jwt.JWTVerifier
import com.example.auth.*
import com.example.auth.request.RegisterRequest
import com.example.auth.response.RegisterResponse
import com.example.db.dao.DAOUsersFacade
import com.example.db.dao.DAOUsersFacadeImpl
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.util.*

fun Application.configureRouting() {

    val userDao: DAOUsersFacade = DAOUsersFacadeImpl()
    val jwtHelper: JWTHelper = JWTHelperImpl()
    val passwordEncryptor: PasswordEncryptor = PasswordEncryptorImpl()

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    install(Authentication) {
        jwt {
            verifier(jwtHelper.verifier)
            validate { credential ->
                credential.payload.getClaim("userId").asInt()?.let { userId ->
                    // do database query to find Principal subclass
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

        post("register") {
            val registerRequest = call.receive<RegisterRequest>()
            // todo добавить проверок на валидность данных
            // todo добавить проверку, чтобы нельзя было регистрировать один и тот же логин

            val user = userDao.addNewUser(
                registerRequest.login,
                passwordEncryptor.encryptPassword(registerRequest.password),
                registerRequest.fullName
            )
            if (user != null) {
                val tokens = jwtHelper.createTokens(user)
                val registerResponse = RegisterResponse(
                    user.id,
                    user.login,
                    user.fullName,
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
