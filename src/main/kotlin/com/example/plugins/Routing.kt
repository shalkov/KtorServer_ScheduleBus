package com.example.plugins

import com.example.Consts.USER_ID
import com.example.auth.*
import com.example.auth.request.AuthRequest
import com.example.auth.request.RefreshTokenRequest
import com.example.auth.request.RegisterRequest
import com.example.auth.response.LoginResponse
import com.example.auth.response.TokenResponse
import com.example.base.generateHttpResponse
import com.example.controllers.RefreshTokenController
import com.example.controllers.SignInController
import com.example.controllers.SignUpController
import com.example.db.dao.users.UsersDao
import com.example.db.models.UserRole
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import org.koin.core.component.inject
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val signInController by inject<SignInController>()
    val signUpController by inject<SignUpController>()
    val refreshTokenController by inject<RefreshTokenController>()
    val userDao by inject<UsersDao>()

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Первый бекенд!")
            //todo сделать отображение Swagger спецификации
        }

        post("auth") {
            val authRequest = call.receive<AuthRequest>()
            val authResponse = signInController.signIn(authRequest)
            val response = generateHttpResponse(authResponse)
            call.respond(response.code, response.body)
        }

        post("register") {
            val registerRequest = call.receive<RegisterRequest>()
            val registerResponse = signUpController.signUp(registerRequest)
            val response = generateHttpResponse(registerResponse)
            call.respond(response.code, response.body)
        }

        post("refresh") {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val refreshTokenResponse = refreshTokenController.refresh(refreshTokenRequest)
            val response = generateHttpResponse(refreshTokenResponse)
            call.respond(response.code, response.body)
        }

        authenticate {
            // Авторизованная зона и роль админ
            withRoles(UserRole.ADMIN.role) {
                get("/admin") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim(USER_ID)?.asInt()
                    val roles = principal?.payload?.getClaim("roles")?.asList(UserRole::class.java)?.toSet()
                    val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())

                    val user = userDao.getUserById(userId ?: 0)
                    call.respondText(
                        "Привет, ${user?.login}! Токен протухнет через: $expiresAt ms.\n" +
                                "$roles"
                    )

                }
            }

            withRoles {
                get("/anyone") {
                    call.respondText { "For anyone" }
                }
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
