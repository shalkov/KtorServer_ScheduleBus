package ru.shalkoff.bus_schedule.plugins

import ru.shalkoff.bus_schedule.Consts.USER_ID
import ru.shalkoff.bus_schedule.auth.JWTHelper
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts.EMPTY_STRING
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
                    val accessToken = adminController.getAccessTokenFromCookie(it) ?: EMPTY_STRING
                    HttpAuthHeader.Single(AuthScheme.Bearer, accessToken)
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Токен не валидный")
            }
        }
    }
}
