package com.example.plugins

import com.example.Consts.ACCESS_TOKEN_PARAM
import com.example.Consts.ACCESS_TOKEN_VALIDITY_ML
import com.example.Consts.AUTH_ENDPOINT
import com.example.Consts.INDEX_ENDPOINT
import com.example.Consts.REFRESH_ENDPOINT
import com.example.Consts.REFRESH_TOKEN_PARAM
import com.example.Consts.REFRESH_TOKEN_VALIDITY_ML
import com.example.Consts.REGISTER_ENDPOINT
import com.example.Consts.USER_ID
import com.example.auth.*
import com.example.auth.request.AuthRequest
import com.example.auth.request.RefreshTokenRequest
import com.example.auth.request.RegisterRequest
import com.example.auth.response.LoginResponse
import com.example.base.BaseResponse
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

    fun saveTokensToCookie(
        authResponse: BaseResponse,
        call: ApplicationCall
    ) {
        if (authResponse is LoginResponse) {
            call.response.cookies.append(
                ACCESS_TOKEN_PARAM,
                authResponse.tokens.accessToken,
                maxAge = ACCESS_TOKEN_VALIDITY_ML,
                httpOnly = true
            )
            call.response.cookies.append(
                REFRESH_TOKEN_PARAM,
                authResponse.tokens.refreshToken,
                maxAge = REFRESH_TOKEN_VALIDITY_ML,
                httpOnly = true
            )
        }
    }

    routing {
        get(INDEX_ENDPOINT) {
            call.respondText("Первый бекенд!")
            //todo сделать отображение Swagger спецификации
        }

        post(AUTH_ENDPOINT) {
            val authRequest = call.receive<AuthRequest>()
            val authResponse = signInController.signIn(authRequest)
            val response = generateHttpResponse(authResponse)
            saveTokensToCookie(authResponse, call)

            call.respond(response.code, response.body)
        }

        post(REGISTER_ENDPOINT) {
            val registerRequest = call.receive<RegisterRequest>()
            val registerResponse = signUpController.signUp(registerRequest)
            val response = generateHttpResponse(registerResponse)
            saveTokensToCookie(registerResponse, call)

            call.respond(response.code, response.body)
        }

        post(REFRESH_ENDPOINT) {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val refreshTokenResponse = refreshTokenController.refresh(refreshTokenRequest)
            val response = generateHttpResponse(refreshTokenResponse)
            call.respond(response.code, response.body)
        }

        authenticate {
            // Авторизованная зона и роль админ
            withRoles(UserRole.ADMIN.roleStr) {
                get("/admin000") {
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
