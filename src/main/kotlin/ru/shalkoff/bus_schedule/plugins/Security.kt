package ru.shalkoff.bus_schedule.plugins

import ru.shalkoff.bus_schedule.Consts.USER_ID
import ru.shalkoff.bus_schedule.auth.JWTHelper
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.AdminController

fun Application.configureSecurity() {

    val jwtHelper by inject<JWTHelper>()
    val adminController by inject<AdminController>()

    install(Authentication) {
        jwt {
            verifier(jwtHelper.verifier)
            validate { credential ->
                credential.payload.getClaim(USER_ID).asInt()?.let { userId ->
                    JWTPrincipal(credential.payload)
                }
            }
            authHeader {
                // вытягивает токен сначала из хедера, если там нет, то пробуем из кук.
                val header = it.request.parseAuthorizationHeader()
                if (header != null) {
                    header
                } else {
                    val accessToken = adminController.getAccessTokenFromCookie(it)
                    if (accessToken != null && jwtHelper.verifyToken(accessToken) != null) {
                        HttpAuthHeader.Single(AuthScheme.Bearer, accessToken)
                    } else {
                        null
                    }
                }
            }
            challenge { defaultScheme, realm ->
                val response = generateHttpResponse(GeneralResponse.unauthorized("Вы не авторизованы"))
                call.respond(response.code, response.body)
            }
        }
    }
}
